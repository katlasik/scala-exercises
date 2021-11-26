package pl.scala.exercises.model

sealed trait CompanyResource {
  def name: String
  def owningDepartmentId: Int
  def isSensitive: Boolean
}

object CompanyResource {

  /**
    * TODO Ex29
    * Implement unapply method matching name, owning department id and isSensitive flag for trait CompanyResource.
    */
  def unapply(c: CompanyResource): Option[(String, Int, Boolean)] = ???

}

case class EmployeeSalary(override val name: String) extends CompanyResource {
  override val owningDepartmentId = 1
  override val isSensitive = true
}

case class DatabasePassword(override val name: String) extends CompanyResource {
  override val owningDepartmentId = 2
  override val isSensitive = true
}

case class ProjectDocumentation(override val name: String) extends CompanyResource {
  override val owningDepartmentId = 2
  override val isSensitive = false
}

case class SecurityProcedure(override val name: String) extends CompanyResource {
  override val owningDepartmentId = 3
  override val isSensitive = false
}

case class SecurityReport(override val name: String, override val isSensitive: Boolean) extends CompanyResource {
  override val owningDepartmentId = 3
}

case class GenericDocument(override val name: String, override val owningDepartmentId: Int, override val isSensitive: Boolean)
    extends CompanyResource
