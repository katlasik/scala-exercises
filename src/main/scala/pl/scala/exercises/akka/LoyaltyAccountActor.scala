package pl.scala.exercises.akka

import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import pl.scala.exercises.akka.LoyaltyAccountActor._
import scala.Ordered._
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.Failure
import scala.util.Success

/**
  * Create actor for managing balance of the  loyalty card customer.
  * Actor should be to handle all Commands from companion object.
  *
  * 1. Implement handling of AwardPoints to allow adding points to the account.
  * 2. Implement GetBalance to allow checking the balance of the account by returning Balance.
  * 3. Implement OrderPrize to allow getting prize for available points.
  *    a) If account has insufficient points you should return OrderRejected with message "Insufficient funds!"
  *    b) If account has enough points you should call prize service to get order the prize.
  *    c) If service returns error you should return OrderRejected with message of the exception.
  *    d) If service returns success you should return message PrizeOrdered.
  *  4. If account is blocked by message BlockAccount it should ignore all messages, except UnblockAccount.
  */

object LoyaltyAccountActor {

  sealed trait Command
  case class OrderPrize(name: String, cost: Points, replyTo: ActorRef[Result]) extends Command
  case class AwardPoints(amount: Points, replyTo: ActorRef[Result]) extends Command
  case class GetBalance(replyTo: ActorRef[Result]) extends Command
  case class BlockAccount(replyTo: ActorRef[Result]) extends Command
  case class UnblockAccount(replyTo: ActorRef[Result]) extends Command

  sealed trait Result
  case object AccountBlocked extends Result
  case object AccountUnblocked extends Result
  case class PointsAwarded(points: Points) extends Result
  case class PrizeOrdered(name: String) extends Result
  case class OrderRejected(reason: String) extends Result
  case class Balance(points: Points) extends Result

  private case class RefundPoints(cost: Points) extends Command

  private case class BehaviourLocked(var points: Points, service: PrizeService, override val context: ActorContext[Command])
      extends AbstractBehavior[Command](context) {

    override def onMessage(msg: Command): Behavior[Command] = ???
  }

  private case class BehaviourUnlocked(var points: Points, service: PrizeService, override val context: ActorContext[Command])
      extends AbstractBehavior[Command](context) {

    override def onMessage(msg: Command): Behavior[Command] = ???
  }

  def apply(service: PrizeService) = Behaviors.setup(context => BehaviourUnlocked(Points(0), service, context))

}

object LoyaltyAccountApp extends App {

  val system = ActorSystem(LoyaltyAccountActor(DummyPrizeService), "ActorSystem")
  implicit val timeout: Timeout = Timeout(3.seconds)
  implicit val scheduler = system.scheduler

  val program = for {
    _ <- system.ask(replyTo => AwardPoints(Points(100), replyTo))
    _ <- system.ask(replyTo => AwardPoints(Points(100), replyTo))
    points <- system.ask(replyTo => GetBalance(replyTo))
    _ = println(s"Awarded points: $points")
    result <- system.ask(replyTo => OrderPrize("watch", Points(200), replyTo))
    _ = println(s"Order result: $result")
    points2 <- system.ask(replyTo => GetBalance(replyTo))
    _ = println(s"Points after order: $points2")
    result2 <- system.ask(replyTo => OrderPrize("watch", Points(200), replyTo))
    _ = println(s"Order2 result: $result2")
  } yield ()

  Await.result(program, 10.seconds)

  system.terminate()

}
