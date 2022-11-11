package pl.scala.exercises.futures

import pl.scala.exercises.model.Department
import pl.scala.exercises.model.Employee
import pl.scala.exercises.model.Salary

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
  def getEmployeesManager(employeeId: Int): Future[Employee] = ???

  /**
    *  TODO Ex44
    *  Calculate sum of all salaries of employees (if in currency passed as argument), which ids are passed as argument.
    *  Add tests.
    */
  def sumSalaries(employeesIds: List[Int], currency: String): Future[Salary] = ???

  /**
    *  TODO Ex45
    *  Get employee and it's current department. If there is no department return None.
    *  Add tests.
    */
  def getEmployeeAndDepartment(employeeId: Int): Future[Option[(Employee, Department)]] = ???

  /**
    *  TODO Ex46
    *  Implement function for updating employee salary. If salary is not bigger that previous salary return failed future with SalaryTooLow exception.
    *  If returned future is failed with NotEnoughFunds exception return Future containing false.
    *  If failed future contains ServerNotAvailable exception retry method 3 times and then fail.
    *  If update is successful return true.
    */

  def giveRaise(employeeId: Int, salary: Salary): Future[Boolean] = ???

}
