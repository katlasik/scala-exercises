package pl.scala.examples

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.BinaryMessage
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.server.Directives.handleWebSocketMessages
import akka.http.scaladsl.server.Directives.path
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.DurationInt
import scala.io.StdIn

sealed trait WSCommand
case class Up(ref: ActorRef[Ok.type]) extends WSCommand
case class Down(ref: ActorRef[Ok.type]) extends WSCommand
case class GetResult(ref: ActorRef[Result]) extends WSCommand
case object Ok
case class Result(value: Int)

object WebsocketsExample extends App {

  val counterActor = Behaviors.setup[WSCommand] { context =>
    var value = 0

    Behaviors.receiveMessage {
      case Up(ref) =>
        value = value + 1
        context.log.info("Incrementing value")
        ref ! Ok
        Behaviors.same
      case Down(ref) =>
        ref ! Ok
        context.log.info("Decrementing value")
        value = value - 1
        Behaviors.same
      case GetResult(ref) =>
        ref ! Result(value)
        Behaviors.same
    }

  }

  `implicit private val system = ActorSystem(counterActor, "WebsocketsExample")
  `implicit val timeout: Timeout = Timeout(3.seconds)

  def greeter: Flow[Message, Message, Any] =
    Flow[Message].map {
      case tm: TextMessage =>
        TextMessage(
          tm.textStream.flatMapConcat{
            case "Up" => Source.future(system.ask(ref => Up(ref))).map(_ => "Ok")
            case "Down" => Source.future(system.ask(ref => Down(ref))).map(_ => "Ok")
            case "GetResult" => Source.future(system.ask(ref => GetResult(ref))).map(_.value.toString)
            case msg => Source.failed(new UnsupportedOperationException(s"Unknown message: $msg"))
          }
        )
      case bm: BinaryMessage =>
        bm.dataStream.runWith(Sink.ignore)
        TextMessage(Source.failed(new UnsupportedOperationException("Binary messages not supported!")))
    }

  val websocketRoute =
    path("ws") {
      handleWebSocketMessages(greeter)
    }

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(websocketRoute)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
