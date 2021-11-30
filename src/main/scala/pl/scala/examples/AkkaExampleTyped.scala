package pl.scala.examples

import TypedCalculatorActor._
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Await
import scala.concurrent.duration._

object TypedCalculatorActor {

  sealed trait Command
  case class Add(a: Int, replyTo: ActorRef[Result]) extends Command
  case class Multiply(a: Int, replyTo: ActorRef[Result]) extends Command
  case class GetResult(replyTo: ActorRef[Result]) extends Command
  case class Result(value: Int)

  def apply(): Behavior[Command] =
    Behaviors.setup(context => TypedCalculatorBehavior(context))

}

private case class TypedCalculatorBehavior(override val context: ActorContext[Command]) extends AbstractBehavior[Command](context) {

  var state = 0

  override def onMessage(msg: Command): Behavior[Command] = msg match {
    case Add(a, replyTo) =>
      context.log.info(s"Adding value: $a")
      state = state + a
      replyTo ! Result(state)
      Behaviors.same
    case Multiply(a, replyTo) =>
      context.log.info(s"Multiplying by value: $a")
      state = state * a
      replyTo ! Result(state)
      Behaviors.same
    case GetResult(replyTo) =>
      replyTo ! Result(state)
      Behaviors.same
  }

}

object CalculatorTyped extends App {

  val system: ActorSystem[Command] = ActorSystem(TypedCalculatorActor(), "Calculator")

  implicit val timeout: Timeout = Timeout(3.seconds)
  implicit val scheduler: Scheduler = system.scheduler

  val program = for {
    _ <- system.ask(replyTo => Add(100, replyTo))
    _ <- system.ask(replyTo => Multiply(2, replyTo))
    result1 <- system.ask(replyTo => Multiply(2, replyTo))
    _ = println(s"Result 1: $result1")
    _ <- system.ask(replyTo => Add(100, replyTo))
    result2 <- (system ? GetResult).mapTo[Result]
    _ = println(s"Result 2: $result2")
  } yield ()

  Await.result(program, 10.seconds)

  system.terminate()
}
