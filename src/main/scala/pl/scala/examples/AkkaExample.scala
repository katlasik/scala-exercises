package pl.scala.examples

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import UntypedCalculatorActor._
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

class UntypedCalculatorActor extends Actor {

  var state: Int = 0

  def readOnlyReceive: Receive = {
    case ReadOnly(false) =>
      context.unbecome()
    case GetResult =>
      sender() ! Result(state)
  }

  override def receive: Receive = {
    case Add(a) =>
      context.system.log.info(s"Adding value: $a");
      state = state + a
    case Multiply(a) =>
      context.system.log.info(s"Multiplying value: $a");
      state = state * a
    case GetResult =>
      sender() ! Result(state)
    case ReadOnly(true) =>
      context.become(readOnlyReceive)
  }
}

object UntypedCalculatorActor {
  case class Add(a: Int)
  case class Multiply(a: Int)
  case object GetResult
  case class ReadOnly(onOff: Boolean)

  case class Result(r: Int)

  def props(): Props = Props(new UntypedCalculatorActor)
}

object Calculator extends App {

  val actorSystem: ActorSystem = ActorSystem("Calculator")

  val calculatorActor = actorSystem.actorOf(UntypedCalculatorActor.props())

  implicit val timeout: Timeout = Timeout(10.millis)

  calculatorActor ! Add(100)
  calculatorActor ! Multiply(2)
  calculatorActor ! Multiply(2)

  val result1 = Await.result((calculatorActor ? GetResult).mapTo[Result], 10.seconds)
  println(s"Result 1: $result1}")
  calculatorActor ! Add(100)
  val result2 = Await.result((calculatorActor ? GetResult).mapTo[Result], 10.seconds)
  println(s"Result 2: $result2")

  actorSystem.terminate()
}
