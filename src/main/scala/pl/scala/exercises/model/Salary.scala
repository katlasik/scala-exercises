package pl.scala.exercises.model

case class Salary(value: Int, currency: String)

object Salary {

  /**
    * TODO Ex36
    * Implement ordering for Salary
    */
  implicit val ordering: Ordering[Salary] = ???

  /**
    * TODO Ex37
    * Implement extension method for Salary called USD. Allow postfix syntax like 100 USD.
    */
  //implicit class SalarySyntax

  val HundredDollars = ??? // 100 USD
  val FiftyDollars = ??? // 50 USD

}
