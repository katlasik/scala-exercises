package pl.scala.examples

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.scaladsl.Consumer
import akka.kafka.ConsumerSettings
import akka.kafka.Subscriptions
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import java.time.Duration
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

object KafkaAlpakkaExample extends App {

  implicit val actorSystem = ActorSystem(Behaviors.empty, "AlpakkaSystem")

  private def createConsumerStream(topic: String, consumerGroup: String = "cg-" + Random.nextInt()): Source[String, Consumer.Control] = {
    val kafkaConsumerSettings = ConsumerSettings.create(actorSystem, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers("0.0.0.0:9092")
      .withGroupId(consumerGroup)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      .withStopTimeout(Duration.ofSeconds(5))

    Consumer.plainSource(kafkaConsumerSettings, Subscriptions.topics(topic))
      .map((consumerRecord) => consumerRecord.value)
  }

  val (control, stream) = createConsumerStream("events").preMaterialize()

  val future = stream.runForeach(e => println(s"Received event: $e"))

  Await.result(future, 60.seconds)

  actorSystem.terminate()
  control.stop()

}
