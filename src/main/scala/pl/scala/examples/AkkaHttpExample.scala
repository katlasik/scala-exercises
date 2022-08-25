package pl.scala.examples

import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.actor.typed.Props
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

import scala.collection.mutable.ListBuffer
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder

import scala.io.StdIn
import scala.concurrent.Future
import scala.concurrent.duration._

final case class TodoItem(id: Int, text: String, done: Boolean)

object TodoItem {
  implicit val encoder: Encoder[TodoItem] = deriveEncoder
  implicit val decoder: Decoder[TodoItem] = deriveDecoder
}

final case class TodoItemCreateRequest(text: String)

object TodoItemCreateRequest {
  implicit val encoder: Encoder[TodoItemCreateRequest] = deriveEncoder
  implicit val decoder: Decoder[TodoItemCreateRequest] = deriveDecoder
}

sealed trait Command
final case class GetItems(ref: ActorRef[List[TodoItem]]) extends Command
final case class AddItem(text: String, ref: ActorRef[TodoItem]) extends Command

trait ToDoService {
  def listItems: Future[List[TodoItem]]

  def add(text: String): Future[TodoItem]
}

/**
  * TODO Implement endpoint for updating items (for example to set is as done).
  */
object TodoListServerExample extends FailFastCirceSupport {

  def route(service: ToDoService): Route =
    concat(
      get {
        pathPrefix("item") {
          complete(service.listItems)
        }
      },
      post {
        entity(as[TodoItemCreateRequest]) { request =>
          complete(service.add(request.text))
        }
      }
    )

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "AkkaHttpExample")
    implicit val executionContext = system.executionContext
    implicit val timeout = Timeout(10.seconds)

    val ToDoActor = system.systemActorOf(
      Behaviors.setup[Command] { context =>
        var idSequence = 0
        val items = ListBuffer[TodoItem]()

        Behaviors.receiveMessage {
          case GetItems(ref) =>
            ref ! items.toList
            Behaviors.same
          case AddItem(text, ref) =>
            context.log.info(s"Adding new item [$text] with id [$idSequence].")
            val item = TodoItem(id = idSequence, text = text, done = false)
            items += item
            idSequence = idSequence + 1
            ref ! item
            Behaviors.same
        }
      },
      "TodoActor",
      Props.empty
    )

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(
      route(new ToDoService {
        override def listItems: Future[List[TodoItem]] = ToDoActor.ask(GetItems)

        override def add(text: String): Future[TodoItem] =
          ToDoActor.ask(ref => AddItem(text, ref))

      })
    )
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
