package pl.scala.exercises.stdlib

import pl.scala.exercises.model._

import java.time.LocalDate
import scala.language.postfixOps
import scala.util.Random

final class StdLibExercises(actions: Actions) {

  /**
    * TODO Ex0
    * Check if temperature is right. Temperature of Celsius scale between 15 and 25 should return true.
    * Otherwise return false. For Fahrenheit scale calculate temperate scale first.
    * If scale is unsupported throw UnsupportedOperationException.
    */
  def checkTemperature(degrees: Double, scale: String): Boolean = {
    def degreesCelsius(degreesFahrenheit: Double) = ???

    ???
  }

  /**
    * TODO Ex1
    * Create function that takes tuple of 3 options. If all options contain element return Some of tuple of 3 elements.
    * If any of elements is None return None
    */
  def traverse[A](t: (Option[A], Option[A], Option[A])): Option[(A, A, A)] = ???

  /**
    * TODO Ex2
    * Create a function that validates value `a` using function `test`.
    * If `test` returns Some containing error return left.
    * Otherwise return right containing value
    */
  def genericValidation[A](test: A => Option[String])(a: A): Either[String, A] = ???

  /**
    * TODO Ex4
    * Implement function for getting emails of all employees.
    */
  def employeesEmails: List[String] = ???

  /**
    * TODO Ex5
    * Implement function for getting tuples of full name and email of all employees. Use method fullName from Employee class.
    */
  def employeesNamesWithEmails: List[(String, String)] = ???

  /**
    * TODO Ex6
    * Implement function returning list of tuples containing list of employee names (fullName) and phone for every phone of each employee.
    *
    * HINT: use flatMap or flatten
    */
  def getAllPhones: List[(String, String)] = ???

  /**
    * TODO Ex7
    * Return list of string containing first and last name of employee sorted alphabetically by the last name.
    */
  def sortedEmployeesNames: List[String] = ???

  /**
    * TODO Ex8
    * Return list of employees as string in following format:
    * 1. John Steward
    * 2. Jane Doe
    * ....
    *
    * HINT: Use zipWithIndex and mkString
    */
  def listOfEmployeesNames: String = ???

  /**
    * TODO Ex9
    * Return list of employees as string in following format:
    * A. John Steward
    * B. Jane Doe
    * ....
    *
    * HINT: Use range, zip and mkString
    */
  def listOfEmployeesNamesWithLetters: String = ???

  /**
    * TODO Ex10
    * Implement function to get maximum and minimum value of salary and return it as an option of tuple (minimum, maximum).
    * Return None is passed list is empty
    */
  def minAndMaxSalary(employees: List[Employee]): Option[(Salary, Salary)] = ???

  /**
    * TODO Ex11
    * Implement function to modify salaries in the list by multiplying value by modifier. Apply modifier1 for lowest 2, and modifier2 for the rest.
    */
  def modifySalaries(salaries: List[Salary], modifier1: Double, modifier2: Double): List[Salary] = ???

  /**
    * TODO Ex12
    * Implement function to two first best earning employees return it as an option of tuple (minimum, maximum).
    */
  def twoBestEarningEmployees: Option[(Employee, Employee)] = ???

  /**
    * TODO Ex14
    * Return list of employees that are active today.
    */
  def getAllActiveEmployees: List[Employee] = ???

  /**
    * TODO Ex17
    * Implement function for getting tuples of employees email and employee instance by domain passed as argument.
    */
  def findUsersByDomain(domain: String): List[(String, Employee)] = ???

  /**
    * TODO Ex19
    * Implement function that return sum of salaries of all top level managers
    */

  def getSumOfTopLevelManagersSalaries(): Int = ???

  /**
    * TODO Ex20
    * Implement function that sends a mail to all employees with departmentId passed as argument.
    *
    * HINT: Use foreach and actions.sendEmail
    */

  def sendMailToAllEmployeesOfDepartment(departmentId: Int, message: String): Unit = ???

  /**
    * TODO Ex21
    * Split employees and return tuple containing 3 lists of employees:
    *  1st list of employees having more than 1 phone
    *  2nd list of employees having more than 1 employment period
    *  3rd list for the rest
    *
    *  HINT: Use foldLeft
    */
  def splitEmployees: (List[Employee], List[Employee], List[Employee]) = ???

  /**
    * TODO Ex22
    * Implement function returning office location of employee as "<building>: <office>" but returning Some only
    * if employee has office.
    * HINT: use flatMap
    */
  def getOfficeOfEmployee(e: Employee): Option[String] = ???

  /**
    * TODO Ex23
    * Implement function that returns all possible pairs of employees names passed as argument.
    * For example for input:
    * Rick, Brooke, Terry
    * return
    * (Rick,Brooke), (Rick,Terry), (Brooke,Rick), (Brooke,Terry), (Terry,Rick), (Terry,Brooke)
    * HINT: use flatMap
    */
  def getPairsOfEmployees(employeeNames: List[String]): List[(String, String)] = ???

  /**
    * TODO Ex25
    * Implement ordering for localData
    */
//  implicit val ordering: Ordering[LocalDate] = ???

  /**
    * TODO Ex26
    * Return list of sorted employees from latest hired. If employee has no employment period they should go last.
    */
  def sortedEmployeesFromNewest: List[Employee] = ???

  /**
    * TODO Ex27
    * Implement function taking salary as argument and returning tuple of two lists:
    * all employees below given salary and all employees above.
    */
  def aboveAndBelowSalary(s: Int): (List[Employee], List[Employee]) = ???

  /**
    * TODO Ex28
    * Implement function that returns the sum of the days employee was employed across all periods for today.
    */
  def employmentDays(employee: Employee): Long = ???

  /**
    * TODO Ex29
    * Implement function returning map of department as key and list of active employees as value.
    * HINT: use method view on map.
    */

  def employeesByDepartment: Map[Department, List[Employee]] = ???

  /**
    * TODO Ex31
    * Calculate average salary per department. Reuse method employeesByDepartment.
    * HINT: use method view on map.
    */
  def salariesByDepartment: Map[Department, Int] = ???

  /**
    * TODO Ex34
    * Get all available resources for user. Use method canAccessResource from user.
    */
  def getAllAvailableResources(user: User): List[CompanyResource] = ???

  /**
    * TODO Ex35
    * Get all available resources for employee. Reuse method getAllAvailableResources.
    */
  def getAllAvailableResourcesForEmployee(employee: Employee): List[CompanyResource] = ???

  /**
    * TODO Ex38
    * Return the highest ranking supervisor of employee (manager of manager of manager ..., etc.). If employee has no manager, just return that employee.
    * Try to make method tail-recursive.
    */
  def getHighestRankingSuperior(employee: Employee): Option[Employee] = ???

}
