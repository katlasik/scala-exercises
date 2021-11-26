package pl.scala.exercises.futures

import pl.scala.exercises.model.Department
import pl.scala.exercises.model.Employee

import scala.concurrent.Future

private[futures] trait Actions {
  def getEmployee(id: Int): Future[Employee]
  def updateEmployee(employee: Employee): Future[Unit]
  def getDepartment(id: Int): Future[Option[Department]]
}
