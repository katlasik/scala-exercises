package pl.scala.exercises.model

import java.time.LocalDate
import scala.language.postfixOps
import scala.util.Try

case class Location(building: String, office: Option[String])
case class Department(id: Int, name: String)
case class EmploymentPeriod(from: LocalDate, to: Option[LocalDate], departmentId: Int)

object EmploymentPeriod {

  /**
    * TODO Ex24
    * Implement function that checks if there are any overlapping employment periods.
    * HINT: use grouped
    */
  def checkIfPeriodsOverlap(periods: Vector[EmploymentPeriod]): Boolean = ???

}

case class Employee(
    id: Int,
    firstName: String,
    lastName: String,
    email: String,
    employmentHistory: Vector[EmploymentPeriod],
    phones: List[String],
    salary: Salary,
    managerId: Option[Int] = None,
    location: Option[Location] = None
) {

  /**
    * TODO Ex2
    * Implement value returning fullName of employee (firstName + lastName)
    */
  val fullName: String = ""

  /**
    * TODO Ex13
    * Implement function that returns true, if employee has employment period for given day.
    */
  def isActiveAt(day: LocalDate): Boolean = ???

  /**
    * TODO Ex15
    * Implement function returning current active department for employee.
    */
  def getActiveDepartment: Option[Department] = ???

}

object Employee {

  private val CompanyDomain = "acme.com"
  private val InitialSalary = Salary(2000, "USD")

  /**
    * TODO Ex3
    * Implement apply method that returns instance of Either[Employee]. In case managerId is equal to employee's id return Left
    * with string "Manager id should be different that employee's id". Use InitialSalary for salary. Use method createEmail to create email
    */
  def apply(id: Int, firstName: String, lastName: String, phone: String, managerId: Option[Int]): Either[String, Employee] = ???
}

/**
  * TODO Ex16
  * Implement `unapply` method that allows matching by email's domain. It should match (domain, whole email).
  */
object EmailDomain {

  def unapply(employee: Employee): Option[(String, String)] = ???
}

/**
  * TODO Ex18
  * Implement unapply function that matches only top-level managers (with empty managerId).
  */
object TopLevelManager {
  def unapply(e: Employee): Option[Employee] = ???
}
