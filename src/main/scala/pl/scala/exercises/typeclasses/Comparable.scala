package pl.scala.exercises.typeclasses

import cats.instances.CharInstances

import scala.util.chaining._

sealed trait CompareResult
case object LeftGreater extends CompareResult
case object Equal extends CompareResult
case object RightGreater extends CompareResult

trait Comparable[T] {

  def compare(left: T, right: T): CompareResult

}

object Comparable {

  def apply[T](implicit c: Comparable[T]): Comparable[T] = c

  implicit val charComparable: Comparable[Char] = (left: Char, right: Char) =>
    left
      .compareTo(right)
      .pipe(r => if (r < 0) LeftGreater else if (r == 0) Equal else RightGreater)

  /**
   * TODO Ex39
   *  Implement comparable for numeric. */
  implicit def numericComparable[T](
      implicit numeric: Numeric[T]
  ): Comparable[T] = ???

  /**
   *   TODO Ex40
   *   Implement comparable for booleans. True is greater that false. Add tests.
   */
  //implicit val booleansComparable

  /**
   *  TODO Ex41
   *  Implement comparable for Option. Option with value is greater than option without value. If both options have value compare value inside.
   *  Write tests.
   */
  //implicit val optionComparable

  /**
   *  TODO Ex42
   *  Implement comparable for string. Longer string is greater. If both string have same length, compare characters from left.
   *  First greater character wins.
   */
  implicit def stringComparable(
      implicit charComparable: Comparable[Char]
  ): Comparable[String] = ???



}
