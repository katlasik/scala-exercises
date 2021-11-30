package pl.scala.exercises.model

import scala.language.implicitConversions

sealed trait User {
  val username: String

  def canAccessResource(resource: CompanyResource): Boolean
}

object User {

  /**
    * TODO Ex33
    * Implement function to change get instance of User from Employee. All employees that are top-level managers should be admin.
    * User's that have active employment period should be RegularUsers, all other should be Guest. Use email as username.
    *
    * Modify function to work as implicit conversion.
    */
  implicit def fromEmployee(e: Employee): User = ???

}

case class Admin(override val username: String) extends User {
  override def canAccessResource(resource: CompanyResource): Boolean = true
}

case class RegularUser(override val username: String, departmentId: Int) extends User {

  /**
    * TODO Ex32
    * Implement function that checks whether employee can access resource.
    * RegularUser can access all resources that are not secret and all resources from its department
    */
  override def canAccessResource(resource: CompanyResource): Boolean = ???
}

case class Guest(override val username: String) extends User {
  override def canAccessResource(resource: CompanyResource): Boolean = false
}
