package pl.scala.exercises.akkastreams

import akka.actor
import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.testkit.TestKit
import akka.testkit.TestProbe
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.flatspec.AnyFlatSpecLike
import akka.pattern._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._

class StreamsExerciseTest extends TestKit(ActorSystem("Tests")) with AnyFlatSpecLike {

  trait Fixture {
    val probe = TestProbe()
  }

  it should "get only Security Events" in new Fixture {

    val streams = new StreamsExercise()

    //given
    val source = Source(
      List(
        Event("Kasia", Security, 1),
        Event("Basia", Commerce, 2),
        Event("Piotr", Security, 3)
      )
    )

    //when
    source.via(streams.pickSecurityEvents).runWith(Sink.seq).pipeTo(probe.ref)

    //then
    probe.expectMsg(
      1.second,
      List(
        Event("Kasia", Security, 1),
        Event("Piotr", Security, 3)
      )
    )
  }

  it should "emit SecurityViolation for user if there are 3 security events with priority 2 or more in row" in new Fixture {

    val streams = new StreamsExercise()

    //given
    val s1 = Source(
      List(
        Event("Kasia", Security, 1),
        Event("Basia", Security, 1),
        Event("Piotr", Security, 2),
        Event("Kasia", Security, 1),
        Event("Kasia", Security, 3),
        Event("Kasia", Security, 3),
        Event("Piotr", Security, 2),
        Event("Kasia", Security, 3),
        Event("Basia", Security, 2),
        Event("Basia", Security, 2),
        Event("Basia", Security, 1),
        Event("Basia", Security, 2),
        Event("Basia", Security, 2),
        Event("Piotr", Security, 1),
      )
    )
    //when
    s1.via(streams.checkForSecurityViolationIn10s).runWith(Sink.seq).pipeTo(probe.ref)

    //then
    probe.expectMsg(
      1.second,
      Vector(
        SecurityViolation("Kasia")
      )
    )

    //given
    val s2 = Source(
      List(
        Event("Kasia", Security, 1),
        Event("Basia", Security, 2),
        Event("Kasia", Security, 1),
        Event("Basia", Security, 2),
        Event("Kasia", Security, 1),
        Event("Basia", Security, 2),

      )
    )
    //when
    s2.via(streams.checkForSecurityViolationIn10s).runWith(Sink.seq).pipeTo(probe.ref)

    //then
    probe.expectMsg(
      1.second,
      Vector(
        SecurityViolation("Basia")
      )
    )

  }

}
