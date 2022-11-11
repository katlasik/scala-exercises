package pl.scala.exercises.akka

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import pl.scala.exercises.akka.LoyaltyAccountActor.AccountBlocked
import pl.scala.exercises.akka.LoyaltyAccountActor.AccountUnblocked
import pl.scala.exercises.akka.LoyaltyAccountActor.AwardPoints
import pl.scala.exercises.akka.LoyaltyAccountActor.Balance
import pl.scala.exercises.akka.LoyaltyAccountActor.BlockAccount
import pl.scala.exercises.akka.LoyaltyAccountActor.GetBalance
import pl.scala.exercises.akka.LoyaltyAccountActor.OrderPrize
import pl.scala.exercises.akka.LoyaltyAccountActor.OrderRejected
import pl.scala.exercises.akka.LoyaltyAccountActor.PointsAwarded
import pl.scala.exercises.akka.LoyaltyAccountActor.PrizeOrdered
import pl.scala.exercises.akka.LoyaltyAccountActor.Result
import pl.scala.exercises.akka.LoyaltyAccountActor.UnblockAccount

import scala.concurrent.Future
import scala.concurrent.Promise
import scala.util.Failure
import scala.util.Success

class LoyaltyAccountActorTest extends AnyFlatSpec with MockFactory {

  def fixture(testCode: ActorTestKit => Unit) = {
    val testKit = ActorTestKit()
    testCode(testKit)
    testKit.shutdownTestKit()
  }

  it should "award points" in fixture { testKit =>
    //given
    val service = mock[PrizeService]
    val actor = testKit.spawn(LoyaltyAccountActor(service))
    val probe = testKit.createTestProbe[Result]

    //when
    actor ! AwardPoints(Points(100), probe.ref)

    //then
    probe.expectMessage(PointsAwarded(Points(100)))

    //when
    actor ! GetBalance(probe.ref)

    //then
    probe.expectMessage(Balance(Points(100)))
  }

  it should "allow ordering prize" in fixture { testKit =>
    //given
    val service = mock[PrizeService]
    (service.orderPrize _).expects("watch").returning(Future.unit)
    val actor = testKit.spawn(LoyaltyAccountActor(service))
    val probe = testKit.createTestProbe[Result]
    actor ! AwardPoints(Points(100), probe.ref)
    probe.receiveMessage()

    //when
    actor ! OrderPrize("watch", Points(100), probe.ref)

    //then
    probe.expectMessage(PrizeOrdered("watch"))

    //when
    actor ! GetBalance(probe.ref)

    //then
    probe.expectMessage(Balance(Points(0)))
  }

  it should "reject ordering prize when insufficient funds" in fixture { testKit =>
    //given
    val service = mock[PrizeService]
    (service.orderPrize _).expects("watch").never()
    val actor = testKit.spawn(LoyaltyAccountActor(service))
    val probe = testKit.createTestProbe[Result]
    actor ! AwardPoints(Points(100), probe.ref)
    probe.receiveMessage()

    //when
    actor ! OrderPrize("watch", Points(150), probe.ref)

    //then
    probe.expectMessage(OrderRejected("Insufficient funds!"))

    //when
    actor ! GetBalance(probe.ref)

    //then
    probe.expectMessage(Balance(Points(100)))
  }

  it should "reject concurrent ordering of multiple prizes" in fixture { testKit =>
    //given
    val service = mock[PrizeService]
    val prizeWatchPromise = Promise[Unit]()

    (service.orderPrize _).expects("watch").returns(prizeWatchPromise.future)
    (service.orderPrize _).expects("book").never()

    val actor = testKit.spawn(LoyaltyAccountActor(service))
    val probe = testKit.createTestProbe[Result]
    actor ! AwardPoints(Points(100), probe.ref)
    probe.receiveMessage()

    //when
    actor ! OrderPrize("watch", Points(100), probe.ref)
    actor ! OrderPrize("book", Points(100), probe.ref)

    //then
    probe.expectMessage(OrderRejected("Insufficient funds!"))

    //when
    prizeWatchPromise.complete(Failure(new IllegalStateException("Out of stock!")))

    //then
    probe.expectMessage(OrderRejected("Out of stock!"))

    //when
    actor ! GetBalance(probe.ref)

    //then
    probe.expectMessage(Balance(Points(100)))
  }

  it should "reject ordering prize when error happens during order" in fixture { testKit =>
    //given
    val service = mock[PrizeService]
    (service.orderPrize _).expects("watch").returning(Future.failed(new IllegalStateException("Out of stock!")))
    val actor = testKit.spawn(LoyaltyAccountActor(service))
    val probe = testKit.createTestProbe[Result]
    actor ! AwardPoints(Points(100), probe.ref)
    probe.receiveMessage()

    //when
    actor ! OrderPrize("watch", Points(100), probe.ref)

    //then
    probe.expectMessage(OrderRejected("Out of stock!"))

    //when
    actor ! GetBalance(probe.ref)

    //then
    probe.expectMessage(Balance(Points(100)))
  }

  it should "reject all messages when account is blocked" in fixture { testKit =>
    //given
    val service = mock[PrizeService]
    (service.orderPrize _).expects("watch").never()
    val actor = testKit.spawn(LoyaltyAccountActor(service))
    val probe = testKit.createTestProbe[Result]
    actor ! AwardPoints(Points(100), probe.ref)
    probe.receiveMessage()

    //when
    actor ! BlockAccount(probe.ref)

    //then
    probe.expectMessage(AccountBlocked)

    //when
    actor ! OrderPrize("watch", Points(100), probe.ref)

    //then
    probe.expectNoMessage

    //when
    actor ! UnblockAccount(probe.ref)

    //then
    probe.expectMessage(AccountUnblocked)

  }

}
