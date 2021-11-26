package pl.scala.exercises.model

import scala.language.implicitConversions

sealed trait User {
  val username: String

  def canAccessResource(resource: CompanyResource): Boolean
}

object User {

  /**
    * TODO Ex32
    * Implement function to change get instance of User from Employee. All employees that are top-level managers should be admin.
    * User's that have active employment period should be RegularUsers, all other should be Guest. Use email as username.
    *
    * Modify function to work as implicit conversion.
    */
  implicit def fromEmployee(e: Employee): User =
    e match {
      case TopLevelManager(e) => Admin(e.email)
      case e => e.getActiveDepartment match {
          case Some(d) => RegularUser(e.email, d.id)
          case None => Guest(e.email)
        }

    }

}

case class Admin(override val username: String) extends User {
  override def canAccessResource(resource: CompanyResource): Boolean = true
}

case class RegularUser(override val username: String, departmentId: Int) extends User {

  /**
    * TODO Ex30
    * Implement function that checks whether employee can access resource.
    * RegularUser can access all resources that are not secret and all resources from its department
    */
  override def canAccessResource(resource: CompanyResource): Boolean = resource match {
    case CompanyResource(_, _, false) => true
    case CompanyResource(_, `departmentId`, _) => true
    case _ => false
  }
}

case class Guest(override val username: String) extends User {
  override def canAccessResource(resource: CompanyResource): Boolean = false
}
