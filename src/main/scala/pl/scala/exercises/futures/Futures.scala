package pl.scala.exercises.futures

import pl.scala.exercises.model.{Department, Employee, Salary}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.control.NoStackTrace

case object ServerNotAvailable extends Throwable with NoStackTrace
case object NotEnoughFunds extends Throwable with NoStackTrace
case object NoManager extends Throwable with NoStackTrace
case object SalaryTooLow extends Throwable with NoStackTrace

class Futures(actions: Actions) {

  /**
    *  TODO Ex43
    * Get employees direct manager. If employee has no manager return failed Future with NoManager exception.
    * Use `action.getEmployee`.
    */
  def getEmployeesManager(employeeId: Int): Future[Employee] = for {
    employee <- actions.getEmployee(employeeId)
    manager <- employee.managerId match {
      case Some(managerId) => actions.getEmployee(managerId)
      case None => Future.failed(NoManager)
    }
  } yield manager

  /**
    *  TODO Ex44
    *  Calculate sum of all salaries of employees (if in currency passed as argument), which ids are passed as argument.
    *  Add tests.
    */
  def sumSalaries(employeesIds: List[Int], currency: String): Future[Salary] = for {
    employees <- Future.sequence(employeesIds.map(actions.getEmployee))
    sum = employees.collect {
      case e if e.salary.currency == currency =>
        e.salary.value
    }.sum
  } yield Salary(sum, currency)

  /**
    *  TODO Ex45
    *  Get employee and it's current department. If there is no department return None.
    *  Add tests.
    */
  def getEmployeeAndDepartment(employeeId: Int): Future[Option[(Employee, Department)]] = for {
    employee <- actions.getEmployee(employeeId)
  } yield employee.getActiveDepartment.map(department => (employee, department))

  /**
    *  TODO Ex46
    *  Implement function for updating employee salary. If salary is not bigger that previous salary return failed future with SalaryTooLow exception.
    *  If returned future is failed with NotEnoughFunds exception return Future containing false.
    *  If failed future contains ServerNotAvailable exception retry method 3 times and then fail.
    *  If update is successful return true.
    */

  def giveRaise(employeeId: Int, salary: Salary): Future[Boolean] = {
    def attempt(retries: Int): Future[Boolean] = for {
      employee <- actions.getEmployee(employeeId)
      result <- if (salary.value <= employee.salary.value)
        Future.failed(SalaryTooLow)
      else
        actions.updateEmployee(employee.copy(salary = salary))
          .map(_ => true)
          .recoverWith {
            case NotEnoughFunds => Future.successful(false)
            case ServerNotAvailable if retries < 3 => attempt(retries + 1)
          }
    } yield result

    attempt(0)
  }

}
