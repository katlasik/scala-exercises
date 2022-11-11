package pl.scala.examples

import akka.Done
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.alpakka.file.scaladsl.FileTailSource
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source

import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration

object AkkaStreamsExample extends App {

  implicit val system = ActorSystem("StreamsSystem")

  val Path = Paths.get("src/main/resources/input/log.txt")
  val PollingInterval = FiniteDuration(250, TimeUnit.MILLISECONDS)
  val MaxLineSize = 8192

  val sourceLines: Source[String, NotUsed] = FileTailSource.lines(Path, MaxLineSize, PollingInterval)

  val flowSplit: Flow[String, String, NotUsed] = Flow[String].mapConcat(_.split("\\s+"))

  val flowToUppercase: Flow[String, String, NotUsed] = Flow[String].map(_.toUpperCase)

  val flowZipWithIndex: Flow[String, String, NotUsed] = Flow[String].zipWithIndex.map {
    case (text, idx) => s"$idx: $text"
  }

  val sinkPrint = Sink.foreach(println)

  val stream: Future[Done] = sourceLines.via(flowSplit).via(flowToUppercase).runWith(sinkPrint)

  Await.result(stream, Duration.Inf)

}
