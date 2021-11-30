package pl.scala.examples

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer

import java.time.{ Duration => JDuration }
import java.util.Properties
import scala.util.Random
import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters._

trait KafkaConfig {
  val KafkaHost = "0.0.0.0"
  val KafkaPort = 9092

}

trait ProducerSupport extends KafkaConfig {
  private lazy val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, s"$KafkaHost:$KafkaPort")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
  props.put(ProducerConfig.RETRIES_CONFIG, "5")

  private lazy val producer = new KafkaProducer[String, String](props)

  def produce(topic: String, payload: String, key: String = null): Future[RecordMetadata] = Future {
    blocking {
      val record = new ProducerRecord[String, String](topic, key, payload)
      producer.send(record).get()
    }
  }
}

trait ConsumerSupport extends KafkaConfig {

  def consume(topic: String, consumerGroup: String = "consumer-" + Random.nextInt())(
      implicit system: ActorSystem
  ): Source[String, NotUsed] = {

    val props: Properties = new Properties()
    props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup)
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100")

    val consumer = new KafkaConsumer[String, String](props)

    val (queue, source) = Source.queue[String](100)
      .preMaterialize()

    Future {
      try {
        consumer.subscribe(List(topic).asJava)

        @tailrec
        def go(): Unit = {
          val records = consumer.poll(JDuration.ofMillis(100))
          records.records(topic).asScala.foreach(r => queue.offer(r.value()))
          go()
        }

        go()
      } catch {
        case e: Exception =>
          queue.fail(e)
          consumer.close()
      } finally {
        consumer.close()
      }
    }
    source
  }

}

object KafkaExample extends App with ProducerSupport with ConsumerSupport {

  implicit val system = ActorSystem("ConsumerSystem")

  val greetings = List("Hello World!", "Hi!", "Dzień dobry", "Guten tag")

  val sourceGreeting: Source[String, NotUsed] = Source.cycle(() => greetings.iterator)

  val producerFuture = sourceGreeting.delay(5.seconds)
    .runWith(
      Sink.foreach( greeting =>
        produce("messages", greeting)
      )
    )

  consume("messages").runWith(Sink.foreach(m => println(s"New message: $m")))

  Await.result(producerFuture, 1.minute)
  system.terminate()
}
