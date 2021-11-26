package pl.scala.exercises.model

case class Salary(value: Int, currency: String)

object Salary {

  /**
    * TODO Ex8a
    * Implement ordering for Salary
    */
  implicit val ordering: Ordering[Salary] = Ordering.by(_.value)

  /**
    * TODO Ex39
    * Implement extension method for Salary called USD. Allow postfix syntax like 100 USD.
    */
  implicit class SalarySyntax(i: Int) {
    def USD = Salary(i, "USD")
  }

}
