package pl.scala.exercises.akkastreams

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.Done
import akka.NotUsed
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Random

sealed trait EventType

case object Security extends EventType
case object Commerce extends EventType

case class Event(user: String, eventType: EventType, priority: Int)

case class SecurityViolation(user: String)

trait UserEvents {

  val Users = List(
    "Tomek",
    "Darek",
    "Ania",
    "Sylwia",
    "Magda"
  )

  private val random = new Random

  private def randomEvent = Event(Users(random.nextInt(Users.size)), if (random.nextBoolean()) Security else Commerce, Random.nextInt(5))
  val eventSource: Source[Event, NotUsed] = Source.fromIterator(() => Iterator.continually(randomEvent))
    .delay(100.millis)

}

class StreamsExercise(implicit system: ActorSystem) extends UserEvents {

  /**
    * Pick only security events
    */
  val pickSecurityEvents: Flow[Event, Event, NotUsed] = Flow[Event].collect {
    case e @ Event(_, Security, _) => e
  }

  /**
    * Group security events for user if there are 3 consecurity security events with priority 2 or more emit SecurityViolation
    */
  val checkForSecurityViolationIn10s: Flow[Event, SecurityViolation, NotUsed] = Flow[Event]
    .groupBy(Users.size, _.user)
    .sliding(3, 1)
    .mapConcat{window =>
      if(window.forall(_.priority >= 2)){
        List(SecurityViolation(window.head.user))
      } else {
        Nil
      }
    }.mergeSubstreams

  def saveViolationToDatabase: Sink[SecurityViolation, Future[Done]] = Sink
    .foreach(e => println(s"Saving event to the database: $e"))

  def start: Future[Done] =eventSource
    .via(pickSecurityEvents)
    .via(checkForSecurityViolationIn10s)
    .runWith(saveViolationToDatabase)

}

object Main extends App {
  implicit val system = ActorSystem("StreamsExercise")
  new StreamsExercise().start
}
