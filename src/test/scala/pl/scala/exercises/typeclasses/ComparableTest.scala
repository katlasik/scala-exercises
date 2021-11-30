package pl.scala.exercises.typeclasses

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

class ComparableTest extends AnyFlatSpec with Matchers with TableDrivenPropertyChecks{

  it should "compare strings" in {
    val cases = Table(
      ("left", "right", "expected"),
      ("1", "12", RightGreater),
      ("12", "1", LeftGreater),
      ("1", "1", Equal),
      ("A", "B", LeftGreater),
      ("B", "A", RightGreater),
      ("BB", "BA", RightGreater),
      ("BBB", "BAB", RightGreater),
    )

    forAll(cases) {
      case (left, right, expected) =>
        Comparable[String].compare(left, right) shouldBe expected
    }

  }

  it should "compare ints" in {
    val cases = Table(
      ("left", "right", "expected"),
      (1, 2, RightGreater),
      (2, 1, LeftGreater),
      (1, 1, Equal)
    )

    forAll(cases) {
      case (left, right, expected) =>
        Comparable[Int].compare(left, right) shouldBe expected
    }

  }



}
