package pl.scala.exercises

import pl.scala.exercises.model._

import java.time.LocalDate
import scala.annotation.tailrec
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.language.postfixOps

final class Exercises(actions: Actions) {

  /**
    * TODO Ex1
    * Implement function for getting emails of all employees.
    */
  def employeesEmails: List[String] = CompanyRepository
    .employees
    .map(_.email)

  /**
    * TODO Ex3
    * Implement function for getting tuples of full name and email of all employees. Use method fullName from Employee class.
    */
  def employeesNamesWithEmails: List[(String, String)] = CompanyRepository
    .employees
    .map(e => (e.fullName, e.email))

  /**
    * TODO Ex4
    * Implement function returning list of tuples containing list of employee names (fullName) and phone for every phone of each employee.
    *
    * HINT: use flatMap or flatten
    */
  def getAllPhones: List[(String, String)] = CompanyRepository.employees
    .flatMap(e => e.phones.map(p => (e.fullName, p)))

  /**
    * TODO Ex5
    * Return list of string containing first and last name of employee sorted alphabetically by the last name.
    */
  def sortedEmployeesNames: List[String] = CompanyRepository.employees
    .sortBy(_.lastName)
    .map(_.fullName)

  /**
    * TODO Ex6
    * Return list of employees as string in following format:
    * 1. John Steward
    * 2. Jane Doe
    * ....
    *
    * HINT: Use zipWithIndex and mkString
    */
  def listOfEmployeesNames: String = sortedEmployeesNames.zip(LazyList.from(1)).map {
    case (name, idx) => s"$idx. $name"
  }.mkString("\n")

  /**
    * TODO Ex7
    * Return list of employees as string in following format:
    * A. John Steward
    * B. Jane Doe
    * ....
    *
    * HINT: Use range, zip and mkString
    */
  def listOfEmployeesNamesWithLetters: String = sortedEmployeesNames.zip('A' to 'Z').map {
    case (name, letter) => s"$letter. $name"
  }.mkString("\n")

  /**
    * TODO Ex8b
    * Implement function to get maximum and minimum value of salary and return it as an option of tuple (minimum, maximum).
    * Return None is passed list is empty
    */
  def minAndMaxSalary(employees: List[Employee]): Option[(Salary, Salary)] = {
    val sorted = employees.map(_.salary).sorted
    sorted.headOption.flatMap(best => sorted.lastOption.map(worst => (best, worst)))
  }

  /**
    * TODO Ex9
    * Implement function to modify salaries in the list by multiplying value by modifier. Apply modifier1 for lowest 2, and modifier2 for the rest.
    */
  def modifySalaries(salaries: List[Salary], modifier1: Double, modifier2: Double): List[Salary] = {

    def modify(v: Double)(salary: Salary): Salary = salary.copy(value = (salary.value * v).toInt)

    val sorted = salaries.sorted

    sorted.dropRight(2).map(modify(modifier2)) ++ sorted.takeRight(2).map(modify(modifier1))
  }

  /**
    * TODO Ex11
    * Implement function to two first best earning employees return it as an option of tuple (minimum, maximum).
    */
  def twoBestEarningEmployees: Option[(Employee, Employee)] = {
    val sorted = CompanyRepository.employees.sortBy(_.salary).takeRight(2)
    sorted match {
      case second :: first :: _ => Some(first, second)
      case _ => None
    }
  }

  /**
    * TODO Ex13
    * Return list of employees that are active today.
    */
  def getAllActiveEmployees: List[Employee] = CompanyRepository.employees
    .filter(_.isActiveAt(LocalDate.now()))

  /**
    * TODO Ex16
    * Implement function for getting tuples of employees email and employee instance by domain passed as argument.
    */
  def findUsersByDomain(domain: String): List[(String, Employee)] = CompanyRepository.employees.collect {
    case employee @ EmailDomain(`domain`, email) => (email, employee)
  }

  /**
    * TODO Ex17b
    * Implement function that return sum of salaries of all top level managers
    */

  def getSumOfTopLevelManagersSalaries(): Int = CompanyRepository.employees.collect {
    case TopLevelManager(e) => e.salary.value
  }.sum

  /**
    * TODO Ex18
    * Implement function that sends a mail to all employees with departmentId passed as argument.
    *
    * HINT: Use foreach
    */

  def sendMailToAllEmployeesOfDepartment(departmentId: Int, message: String): Unit = CompanyRepository.employees
    .filter(_.getActiveDepartment.exists(d => d.id == departmentId))
    .foreach(e => actions.sendMail(e.email, message))

  /**
    * TODO Ex19
    * Split employees and return tuple containing 3 lists of employees:
    *  1st list of employees having more than 1 phone
    *  2nd list of employees having more than 1 employment period
    *  3rd list for the rest
    *
    *  HINT: Use foldLeft
    */
  def splitEmployees: (List[Employee], List[Employee], List[Employee]) = CompanyRepository
    .employees
    .foldLeft((List.empty[Employee], List.empty[Employee], List.empty[Employee])) {
      case ((accounting, engineering, security), employee) =>
        employee match {
          case e if e.phones.size > 1 => (accounting :+ employee, engineering, security)
          case e if e.employmentHistory.size > 1 => (accounting, engineering :+ employee, security)
          case _ => (accounting, engineering, security :+ employee)
        }
    }

  /**
    * TODO Ex20
    * Implement function returning office location of employee as "<building>: <office>" but returning Some only
    * if employee has office.
    * HINT: use flatMap
    */
  def getOfficeOfEmployee(e: Employee): Option[String] = for {
    l <- e.location
    o <- l.office
  } yield o

  /**
    * TODO Ex22
    * Implement function that returns all possible pairs of employees names passed as argument.
    * For example for input:
    * Rick, Brooke, Terry
    * return
    * (Rick,Brooke), (Rick,Terry), (Brooke,Rick), (Brooke,Terry), (Terry,Rick), (Terry,Brooke)
    * HINT: use flatMap
    */
  def getPairsOfEmployees(employeeNames: List[String]): List[(String, String)] = for {
    e1 <- employeeNames
    e2 <- employeeNames if e1 != e2
  } yield (e1, e2)

  /**
    * TODO Ex21
    * Implement ordering for localData
    */
  implicit val ordering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  /**
    * TODO Ex24
    * Return list of sorted employees from latest hired. If employee has no employment period they should go last.
    */
  def sortedEmployeesFromNewest: List[Employee] = CompanyRepository.employees
    .sortBy(_.employmentHistory.map(_.from).sorted.headOption).reverse

  /**
    * TODO Ex25
    * Implement function taking salary as argument and returning tuple of two lists:
    * all employees below given salary and all employees above.
    */
  def aboveAndBelowSalary(s: Int): (List[Employee], List[Employee]) = CompanyRepository.employees
    .partition(_.salary.value < s)

  /**
    * TODO Ex26
    * Implement function that returns the sum of the days employee was employed across all periods for today.
    */
  def employmentDays(employee: Employee): Long =
    employee.employmentHistory.map {
      case EmploymentPeriod(from, to, _) =>
        to.getOrElse(LocalDate.now()).toEpochDay - from.toEpochDay
    }.sum

  /**
    * TODO Ex27
    * Implement function returning map of department as key and list of active employees as value.
    * HINT: use method view on map.
    */

  def employeesByDepartment: Map[Department, List[Employee]] = {
    CompanyRepository.employees
      .groupBy(_.getActiveDepartment)
      .view
      .collect {
        case (Some(department), employees) => department -> employees
      }.toMap
  }

  /**
    * TODO Ex28
    * Calculate average salary per department. Reuse method employeesByDepartment.
    * HINT: use method view on map.
    */
  def salariesByDepartment: Map[Department, Int] = employeesByDepartment
    .view
    .mapValues(s => s.map(_.salary.value).sum / s.size)
    .toMap

  /**
    * TODO Ex31
    * Get all available resources for user. Use method canAccessResource from user.
    */
  def getAllAvailableResources(user: User): List[CompanyResource] =
    CompanyRepository.resources.filter(user.canAccessResource)

  /**
    * TODO Ex33
    * Get all available resources for employee. Reuse method getAllAvailableResources.
    */
  def getAllAvailableResourcesForEmployee(employee: Employee): List[CompanyResource] =
    getAllAvailableResources(User.fromEmployee(employee))

  /**
    * TODO Ex34
    * Return the highest ranking supervisor of employee (manager of manager of manager ..., etc.). If employee has no manager, just return that employee.
    * Try to make method tail-recursive.
    */
  @tailrec
  def getHighestRankingSuperior(employee: Employee): Option[Employee] = {
    employee.managerId match {
      case Some(id) =>
        CompanyRepository.employees.find(_.id == id) match {
          case Some(supervisor) => getHighestRankingSuperior(supervisor)
          case None => None
        }
      case None => Some(employee)
    }
  }

  /**
    * TODO Ex37
    * Implement function for updating employees salaries.
    *
    * Use method actions.calculateRaise to new value of salary for every passed employee. Then send changes to remote
    * server using method batchUpdateEmployeesInRemoteService
    */
  def moveEmployeeToDepartment(employee: Employee, department: Department)(
      implicit ec: ExecutionContext
  ): Future[Unit] = for {
    withClosedLastPeriod <- Future.fromTry(employee.endLastEmploymentPeriod(LocalDate.now()))
    withNewPeriod <- Future.fromTry(withClosedLastPeriod.withNewEmploymentPeriod(LocalDate.now(), department))
    _ <- actions.updateEmployees(List(withNewPeriod))
  } yield ()

  /**
    * TODO Ex38
    * Implement function for updating employees salaries.
    *
    * Use method actions.calculateRaise to new value of salary for every passed employee. Then send changes to remote
    * server using method batchUpdateEmployeesInRemoteService
    */
  def giveEmployeesRaise(employees: List[Employee])(
      implicit ec: ExecutionContext
  ): Future[Unit] = for {
    updatedSalaries <- Future.sequence(employees.map(e => actions.calculateRaise(e.id)))
    updatedEmployees = employees.zip(updatedSalaries).map {
      case (e, s) => e.copy(salary = s)
    }
    _ <- actions.updateEmployees(updatedEmployees)
  } yield ()

}
