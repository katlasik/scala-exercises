package pl.scala.exercises

import pl.scala.exercises.model._

import scala.concurrent.Future

trait Actions {
  def sendMail(email: String, message: String): Unit

  def calculateRaise(id: Int): Future[Salary]

  def updateEmployees(employees: List[Employee]): Future[Unit]

}
