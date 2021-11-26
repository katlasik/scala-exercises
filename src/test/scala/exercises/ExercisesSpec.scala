package exercises

import org.scalatest.flatspec.AnyFlatSpec
import org.scalamock.scalatest.MockFactory
import org.scalatest.Inside
import org.scalatest.concurrent.Futures
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import pl.scala.exercises.Actions
import pl.scala.exercises.CompanyRepository
import pl.scala.exercises.Exercises
import pl.scala.exercises.model._

import scala.concurrent.ExecutionContext.Implicits._
import java.time.LocalDate
import scala.concurrent.Future

class ExercisesSpec extends AnyFlatSpec with MockFactory with Matchers with Inside with ScalaFutures {

  trait Fixture {

    val actions = mock[Actions]

    val exercises = new Exercises(actions)

  }

  it should "get emails of all email - Ex1" in new Fixture {

    exercises.employeesEmails should contain allOf (
      "joe.doe@acme.com",
      "jane.smith@acme.com",
      "bill.johnson@acme.com",
      "kate.williams@acme.com",
      "ann.smith@external.com",
      "joe.williams@acme.com",
      "gary.newman@acme.com",
      "tony.lopez@acme.com"
    )

  }

  it should "get full name of employee - Ex2" in new Fixture {

    val e = Employee(1, "Tony", "Lopez", "tony.lopez@acme.com", Vector.empty, Nil, Salary(100, "USD"), None, None)

    e.fullName shouldBe "Tony Lopez"

  }

  it should "get emails of all employees with emails - Ex3" in new Fixture {

    exercises.employeesNamesWithEmails should contain allOf (
      ("Joe Doe", "joe.doe@acme.com"),
      ("Jane Smith", "jane.smith@acme.com"),
      ("Bill Johnson", "bill.johnson@acme.com"),
      ("Kate Williams", "kate.williams@acme.com"),
      ("Ann Smith", "ann.smith@external.com"),
      ("Joe Williams", "joe.williams@acme.com"),
      ("Gary Newman", "gary.newman@acme.com"),
      ("Tony Lopez", "tony.lopez@acme.com")
    )

  }

  it should "get emails of all employees with emails - Ex4" in new Fixture {

    exercises.getAllPhones should contain allOf (
      ("Jane Smith", "444"),
      ("Jane Smith", "555234123"),
      ("Bill Johnson", "421"),
      ("Bill Johnson", "785123123"),
      ("Bill Johnson", "444333123"),
      ("Kate Williams", "342"),
      ("Kate Williams", "111222333"),
      ("Ann Smith", "342"),
      ("Ann Smith", "111222333"),
      ("Joe Williams", "942"),
      ("Gary Newman", "884343123"),
      ("Tony Lopez", "884343123")
    )

  }

  it should "get sorted names of employees - Ex5" in new Fixture {

    exercises.sortedEmployeesNames shouldBe List(
      "Joe Doe",
      "Bill Johnson",
      "Tony Lopez",
      "Gary Newman",
      "Jane Smith",
      "Ann Smith",
      "Kate Williams",
      "Joe Williams"
    )
  }

  it should "get list of employee names - Ex6" in new Fixture {

    exercises.listOfEmployeesNames shouldBe """|1. Joe Doe
                                               |2. Bill Johnson
                                               |3. Tony Lopez
                                               |4. Gary Newman
                                               |5. Jane Smith
                                               |6. Ann Smith
                                               |7. Kate Williams
                                               |8. Joe Williams""".stripMargin
  }

  it should "get list of employee names with letters - Ex7" in new Fixture {

    exercises.listOfEmployeesNamesWithLetters shouldBe """|A. Joe Doe
                                                          |B. Bill Johnson
                                                          |C. Tony Lopez
                                                          |D. Gary Newman
                                                          |E. Jane Smith
                                                          |F. Ann Smith
                                                          |G. Kate Williams
                                                          |H. Joe Williams""".stripMargin
  }

  it should "lowest and highest salary - Ex8" in new Fixture {

    exercises.minAndMaxSalary(CompanyRepository.employees) shouldBe Some((Salary(3000, "USD"), Salary(9100, "USD")))

    exercises.minAndMaxSalary(Nil) shouldBe None

  }

  it should "modify salaries - Ex9" in new Fixture {

    exercises.modifySalaries(List(Salary(3000, "USD"), Salary(10000, "USD"), Salary(1000, "USD")), 1.2, 1.5) shouldBe List(
      Salary(1500, "USD"),
      Salary(3600, "USD"),
      Salary(12000, "USD")
    )

  }

  it should "allow creating new instances of Employee - Ex10" in new Fixture {

    Employee(1, "Bill", "Hunter", "5552", None).isRight shouldBe true

  }

  it should "get lowest and highest salary - Ex11" in new Fixture {

    inside(exercises.twoBestEarningEmployees) {
      case Some((first, second)) =>
        first.salary.value shouldBe 9100
        second.salary.value shouldBe 9000

    }

  }

  it should "return true if employee is active - Ex12" in new Fixture {

    val now = LocalDate.now()

    val e = Employee(
      1,
      "Tony",
      "Lopez",
      "tony.lopez@acme.com",
      Vector(
        EmploymentPeriod(
          from = now.minusDays(1),
          to = Some(now.plusDays(2)),
          departmentId = 1
        )
      ),
      Nil,
      Salary(100, "USD"),
      None,
      None
    )

    e.isActiveAt(now.minusDays(1)) shouldBe true
    e.isActiveAt(now.plusDays(2)) shouldBe true
    e.isActiveAt(now) shouldBe true

    e.isActiveAt(now.minusDays(2)) shouldBe false
    e.isActiveAt(now.plusDays(3)) shouldBe false

  }

  it should "get all active employee - Ex13" in new Fixture {

    exercises.getAllActiveEmployees.map(_.fullName) shouldBe List(
      "Jane Smith",
      "Bill Johnson",
      "Kate Williams",
      "Joe Williams",
      "Gary Newman",
      "Tony Lopez"
    )

  }

  it should "get active department of employee - Ex14" in new Fixture {

    val now = LocalDate.now()

    val e = Employee(
      1,
      "Tony",
      "Lopez",
      "tony.lopez@acme.com",
      Vector(
        EmploymentPeriod(
          from = now.minusDays(10),
          to = Some(now.plusDays(2)),
          departmentId = 1
        ),
        EmploymentPeriod(
          from = now.minusDays(1),
          to = Some(now.plusDays(2)),
          departmentId = 1
        )
      ),
      Nil,
      Salary(100, "USD"),
      None,
      None
    )

    inside(e.getActiveDepartment) {
      case Some(Department(id, _)) => id shouldBe 1
    }

  }

  it should "get all employees email by domain - Ex16" in new Fixture {

    exercises.findUsersByDomain("acme.com").map {
      case (email, employee) => (email, employee.fullName)
    } shouldBe List(
      ("joe.doe@acme.com", "Joe Doe"),
      ("jane.smith@acme.com", "Jane Smith"),
      ("bill.johnson@acme.com", "Bill Johnson"),
      ("kate.williams@acme.com", "Kate Williams"),
      ("joe.williams@acme.com", "Joe Williams"),
      ("gary.newman@acme.com", "Gary Newman"),
      ("tony.lopez@acme.com", "Tony Lopez")
    )

    exercises.findUsersByDomain("external.com").map {
      case (email, employee) => (email, employee.fullName)
    } shouldBe List(("ann.smith@external.com", "Ann Smith"))

  }

  it should "sum of managers salaries - Ex17" in new Fixture {

    exercises.getSumOfTopLevelManagersSalaries shouldBe 30100

  }

  it should "send email to all employees by department - Ex18" in new Fixture {

    (actions.sendMail _).expects("jane.smith@acme.com", "hello")
    (actions.sendMail _).expects("bill.johnson@acme.com", "hello")
    (actions.sendMail _).expects("joe.williams@acme.com", "hello")
    (actions.sendMail _).expects("gary.newman@acme.com", "hello")
    (actions.sendMail _).expects("tony.lopez@acme.com", "hello")

    exercises.sendMailToAllEmployeesOfDepartment(2, "hello")

  }

  it should "split employees - Ex19" in new Fixture {

    inside(exercises.splitEmployees) {
      case (moreThanOnePhone, moreThanOnePeriod, rest) =>
        moreThanOnePhone.map(_.fullName) shouldBe List("Jane Smith", "Bill Johnson", "Kate Williams", "Ann Smith")
        moreThanOnePeriod.map(_.fullName) shouldBe List("Joe Doe")
        rest.map(_.fullName) shouldBe List("Joe Williams", "Gary Newman", "Tony Lopez")
    }

  }

  it should "get office of employee - Ex20" in new Fixture {

    val e = Employee(
      1,
      "Tony",
      "Lopez",
      "tony.lopez@acme.com",
      Vector.empty,
      Nil,
      Salary(100, "USD"),
      None,
      Some(Location("1", Some("42")))
    )

    exercises.getOfficeOfEmployee(e) shouldBe Some("42")

  }

  it should "get pairs of all employees - Ex22" in new Fixture {

    exercises.getPairsOfEmployees(List("Rick", "Brooke", "Terry")) shouldBe List(
      ("Rick", "Brooke"),
      ("Rick", "Terry"),
      ("Brooke", "Rick"),
      ("Brooke", "Terry"),
      ("Terry", "Rick"),
      ("Terry", "Brooke")
    )

  }

  it should "check if periods overlap - Ex23" in new Fixture {

    val now = LocalDate.now()

    val ep1 = EmploymentPeriod(
      from = now.minusDays(2),
      to = Some(now.plusDays(2)),
      departmentId = 1
    )

    val ep2 = EmploymentPeriod(
      from = now.minusDays(3),
      to = Some(now.plusDays(1)),
      departmentId = 1
    )

    val ep3 = EmploymentPeriod(
      from = now.minusDays(13),
      to = Some(now.minusDays(11)),
      departmentId = 1
    )

    EmploymentPeriod.checkIfPeriodsOverlap(Vector(ep1, ep2)) shouldBe true
    EmploymentPeriod.checkIfPeriodsOverlap(Vector(ep2, ep3)) shouldBe false

  }

  it should "get sorted employees from newest - Ex24" in new Fixture {

    exercises.sortedEmployeesFromNewest.map(_.fullName) shouldBe List(
      "Bill Johnson",
      "Kate Williams",
      "Joe Williams",
      "Jane Smith",
      "Joe Doe",
      "Gary Newman",
      "Tony Lopez",
      "Ann Smith"
    )
  }

  it should "split employees on salary - Ex25" in new Fixture {

    val (left, right) = exercises.aboveAndBelowSalary(4000)

    left.map(_.fullName) shouldBe List("Joe Doe", "Jane Smith", "Bill Johnson")
    right.map(_.fullName) shouldBe List("Kate Williams", "Ann Smith", "Joe Williams", "Gary Newman", "Tony Lopez")
  }

  it should "get employment days - Ex26" in new Fixture {

    val now = LocalDate.now()

    //given
    val e1 = Employee(
      1,
      "Tony",
      "Lopez",
      "tony.lopez@acme.com",
      Vector(
        EmploymentPeriod(
          from = now.minusDays(204),
          to = Some(now.minusDays(203)),
          departmentId = 1
        ),
        EmploymentPeriod(
          from = now.minusDays(200),
          to = Some(now.minusDays(2)),
          departmentId = 1
        ),
        EmploymentPeriod(
          from = now.minusDays(1),
          to = Some(now),
          departmentId = 1
        )
      ),
      Nil,
      Salary(100, "USD"),
      None,
      None
    )

    val e2 = Employee(
      1,
      "Tony",
      "Lopez",
      "tony.lopez@acme.com",
      Vector.empty,
      Nil,
      Salary(100, "USD"),
      None,
      None
    )

    //when
    exercises.employmentDays(e1) shouldBe 200
    exercises.employmentDays(e2) shouldBe 0
  }

  it should "return employees by departments - Ex27" in new Fixture {

    exercises.employeesByDepartment.view.map {
      case (d, e) => d.name -> e.map(_.fullName)
    }.toMap shouldBe
      Map(
        "Engineering" -> List("Jane Smith", "Bill Johnson", "Joe Williams", "Gary Newman", "Tony Lopez"),
        "Security" -> List("Kate Williams")
      )
  }

  it should "return salaries by departments - Ex28" in new Fixture {

    exercises.salariesByDepartment.view.map {
      case (d, s) => d.name -> s
    }.toMap shouldBe
      Map(
        "Engineering" -> 5720,
        "Security" -> 5000
      )
  }

  it should "get resources for user - Ex31" in new Fixture {

    exercises.getAllAvailableResources(Admin("admin")) shouldBe List(
      EmployeeSalary("developers' salaries"),
      EmployeeSalary("CEO salary"),
      DatabasePassword("main database password"),
      ProjectDocumentation("project docs"),
      SecurityProcedure("very important procedure"),
      SecurityReport("public report", false),
      SecurityReport("secret report", true)
    )

    exercises.getAllAvailableResources(RegularUser("user", 3)) shouldBe List(
      ProjectDocumentation("project docs"),
      SecurityProcedure("very important procedure"),
      SecurityReport("public report", false),
      SecurityReport("secret report", true)
    )

  }

  it should "get resources for employee - Ex33" in new Fixture {

    val e = Employee(
      1,
      "Tony",
      "Lopez",
      "tony.lopez@acme.com",
      Vector.empty,
      Nil,
      Salary(100, "USD"),
      None,
      None
    )

    exercises.getAllAvailableResourcesForEmployee(e) shouldBe List(
      EmployeeSalary("developers' salaries"),
      EmployeeSalary("CEO salary"),
      DatabasePassword("main database password"),
      ProjectDocumentation("project docs"),
      SecurityProcedure("very important procedure"),
      SecurityReport("public report", false),
      SecurityReport("secret report", true)
    )

  }

  it should "get highest ranking supervisor - Ex34" in new Fixture {

    exercises.getHighestRankingSuperior(CompanyRepository.employees.find(_.id == 3).get).map(_.fullName) shouldBe Some("Joe Williams")

  }

  it should "move employee to department - Ex37" in new Fixture {

    val now = LocalDate.now()

    val e = Employee(
      1,
      "Tony",
      "Lopez",
      "tony.lopez@acme.com",
      Vector(
        EmploymentPeriod(
          from = now.minusDays(204),
          to = None,
          departmentId = 1
        )
      ),
      Nil,
      Salary(100, "USD"),
      None,
      None
    )

    val updated = Employee(
      1,
      "Tony",
      "Lopez",
      "tony.lopez@acme.com",
      Vector(
        EmploymentPeriod(
          from = now.minusDays(204),
          to = Some(now),
          departmentId = 1
        ),
        EmploymentPeriod(
          from = now,
          to = None,
          departmentId = 2
        )
      ),
      Nil,
      Salary(100, "USD"),
      None,
      None
    )

    (actions.updateEmployees _).expects(List(updated)).returning(Future.unit)

    exercises.moveEmployeeToDepartment(e, Department(2, "Accounting")).futureValue

  }

  it should "give employees a raise - Ex38" in new Fixture {

    val now = LocalDate.now()

    val e1 = Employee(
      1,
      "Tony",
      "Lopez",
      "tony.lopez@acme.com",
      Vector(
        EmploymentPeriod(
          from = now.minusDays(204),
          to = None,
          departmentId = 1
        )
      ),
      Nil,
      Salary(100, "USD"),
      None,
      None
    )

    val e2 = Employee(
      2,
      "Tony",
      "Alvares",
      "tony.alvares@acme.com",
      Vector.empty,
      Nil,
      Salary(2100, "USD"),
      None,
      None
    )

    val updated1 = e1.copy(salary = Salary(210, "USD"))
    val updated2 = e2.copy(salary = Salary(2310, "USD"))

    (actions.calculateRaise _).expects(1).returning(Future.successful(Salary(210, "USD")))
    (actions.calculateRaise _).expects(2).returning(Future.successful(Salary(2310, "USD")))

    (actions.updateEmployees _).expects(List(updated1, updated2)).returning(Future.unit)

    exercises.giveEmployeesRaise(List(e1, e2)).futureValue
  }
}
