package pl.scala.exercises.model

import pl.scala.exercises.CompanyRepository
import pl.scala.exercises.model.Salary.SalarySyntax

import java.time.LocalDate
import scala.language.postfixOps
import scala.util.Failure
import scala.util.Success
import scala.util.Try

case class Location(building: String, office: Option[String])
case class Department(id: Int, name: String)
case class EmploymentPeriod(from: LocalDate, to: Option[LocalDate], departmentId: Int)

object EmploymentPeriod {

  /**
    * TODO Ex23
    * Implement function that checks if there are any overlapping employment periods.
    * HINT: use grouped
    */
  def checkIfPeriodsOverlap(periods: Vector[EmploymentPeriod]): Boolean =
    periods
      .sortBy(_.from)
      .grouped(2)
      .exists {
        case Vector(prev, next) =>
          prev.to match {
            case Some(to) => next.from.isBefore(to)
            case None => false
          }
        case _ => false
      }

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
  val fullName: String = firstName + " " + lastName

  /**
    * TODO Ex12
    * Implement function that returns true, if employee has employment period for given day.
    */
  def isActiveAt(day: LocalDate): Boolean =
    employmentHistory.exists {
      case EmploymentPeriod(from, to, _) =>
        (from.isBefore(day) || from.isEqual(day)) && to.forall(d => day.isBefore(d) || day.isEqual(d))
    }

  /**
    * TODO Ex14
    * Implement function returning current active department for employee.
    */
  def getActiveDepartment: Option[Department] = {
    val activePeriodToDepartmentId: PartialFunction[EmploymentPeriod, Int] = {
      case EmploymentPeriod(from, to, departmentId) if from.isBefore(LocalDate.now()) && to.fold(true)(LocalDate.now().isBefore) =>
        departmentId
    }

    val maybeDepartmentId = employmentHistory.collectFirst(activePeriodToDepartmentId)

    maybeDepartmentId.flatMap(depId => CompanyRepository.departments.find(_.id == depId))
  }

  /**
    * TODO Ex35
    * Implement function that updates last employment period of employee and sets the "to" date to value passed as argument.
    * Update "to" value only if it's None. If it's Some leave it unchanged.
    * If employee has no employment periods return Failure with IllegalStateException having error message: "No periods!"
    *
    * If validation is successful return Success with new instance of Employee.
    */
  def endLastEmploymentPeriod(endDate: LocalDate): Try[Employee] = employmentHistory match {
    case Vector() => Failure(new IllegalStateException("No periods!"))
    case values :+ last if last.to.isEmpty => Success(copy(employmentHistory = values :+ last.copy(to = Some(endDate))))
    case _ => Success(this)
  }

  /**
    * TODO Ex36
    * Implement function creating a new instance of Employee with new employment period added. New employment period can't
    * overlap already existing periods. In that case return Failure with IllegalStateException having error message: "Overlapping periods!".
    * Additionally there can be only 1 period with unspecified end date. In case such period already exists return Failure with
    * IllegalStateException with message "Only 1 opened period allowed!"
    *
    * If validation is successful return Success with new instance of Employee.
    */
  def withNewEmploymentPeriod(from: LocalDate, department: Department): Try[Employee] = employmentHistory.sortBy(_.from) match {
    case _ :+ last if last.to.isEmpty => Failure(new IllegalStateException("Only 1 opened period allowed!"))
    case _ =>
      if (EmploymentPeriod.checkIfPeriodsOverlap(employmentHistory)) {
        Failure(new IllegalStateException("Overlapping periods!"))
      } else {
        Success(copy(employmentHistory = employmentHistory :+ EmploymentPeriod(from, None, department.id)))
      }
  }

}

object Employee {

  private val CompanyDomain = "acme.com"
  private val InitialSalary = 2000 USD

  /**
    * TODO Ex10
    * Implement apply method that returns instance of Either[Employee]. In case managerId is equal to employee's id return Left
    * with string "Manager id should be different that employee's id". Use InitialSalary for salary. Use method createEmail to create email
    */
  def apply(id: Int, firstName: String, lastName: String, phone: String, managerId: Option[Int]): Either[String, Employee] = {

    def createEmail(firstName: String, lastName: String): String = s"${firstName.toLowerCase}.${lastName.toLowerCase}@$CompanyDomain"

    if (managerId.contains(id))
      Left("Manager id should be different that employee's id")
    else
      Right(new Employee(id, firstName, lastName, createEmail(firstName, lastName), Vector.empty, List(phone), InitialSalary, managerId))
  }
}

/**
  * TODO Ex15
  * Implement `unapply` method that allows matching by email's domain. It should match (domain, whole email).
  */
object EmailDomain {

  def unapply(employee: Employee): Option[(String, String)] = employee.email.split("@") match {
    case Array(_, domain) => Some((domain, employee.email))
    case _ => None
  }
}

/**
  * TODO Ex17a
  * Implement unapply function that matches only top-level managers (with empty managerId).
  */
object TopLevelManager {
  def unapply(e: Employee): Option[Employee] = e.managerId.fold(Option(e))(_ => None)
}
