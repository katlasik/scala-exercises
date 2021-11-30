package pl.scala.exercises.stdlib

import org.scalamock.scalatest.MockFactory
import org.scalatest.Inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import pl.scala.exercises.futures.Actions
import pl.scala.exercises.model._

import java.time.LocalDate

class StdLibExercisesSpec extends AnyFlatSpec with MockFactory with Matchers with Inside with TableDrivenPropertyChecks {

  trait Fixture {

    val actions = mock[Actions]

    val exercises = new StdLibExercises(actions)

  }

  it should "check temperature" in new Fixture {

    val cases = Table(
      ("scale", "temperature", "result"),
      ("celcius", 0.0, false),
      ("celcius", 26.0, false),
      ("celcius", 15.0, true),
      ("celcius", 25.0, true),
      ("fahrenheit", 32.0, false),
      ("fahrenheit", 80.8, false),
      ("fahrenheit", 59.0, true),
      ("fahrenheit", 77.0, true)
    )

    forEvery(cases) {
      case (scale, temperature, result) =>
        exercises.checkTemperature(temperature, scale) shouldBe result
    }

  }

  it should "throw error for wrong scala" in new Fixture {

    intercept[IllegalArgumentException] {
      exercises.checkTemperature(0, "wrong")
    }
  }

  it should "return traverse tuple" in new Fixture {

    exercises.traverse(Some(1), Some(2), Some(3)) shouldBe Some((1, 2, 3))
    exercises.traverse(Some(1), Some(2), None) shouldBe None
    exercises.traverse(Some(1), None, None) shouldBe None
    exercises.traverse(None, Some(2), None) shouldBe None
    exercises.traverse(None, None, None) shouldBe None

  }

  it should "correctly validate" in new Fixture {

    val intValidation: Int => Either[String, Int] =
      exercises.genericValidation[Int](a => Option.when(a < 0)("A must be greater than 0"))

    intValidation(-1) shouldBe Left("A must be greater than 0")
    intValidation(1) shouldBe Right(1)

    val textValidation: String => Either[String, String] =
      exercises.genericValidation[String](a => Option.when(a.isEmpty)("Text must be not empty"))
    textValidation("") shouldBe Left("Text must be not empty")
    textValidation("a") shouldBe Right("a")
  }

  it should "allow creating new instances of Employee" in new Fixture {
    Employee(1, "Bill", "Hunter", "5552", None).isRight shouldBe true
  }


  it should "get emails of all email" in new Fixture {

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

  it should "get full name of employee" in new Fixture {

    val e = Employee(1, "Tony", "Lopez", "tony.lopez@acme.com", Vector.empty, Nil, Salary(100, "USD"), None, None)

    e.fullName shouldBe "Tony Lopez"

  }

  it should "get emails of all employees with emails" in new Fixture {

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

  it should "get phones of all employees with phones" in new Fixture {

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

  it should "get sorted names of employees" in new Fixture {

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

  it should "get list of employee names" in new Fixture {

    exercises.listOfEmployeesNames shouldBe """|1. Joe Doe
                                               |2. Bill Johnson
                                               |3. Tony Lopez
                                               |4. Gary Newman
                                               |5. Jane Smith
                                               |6. Ann Smith
                                               |7. Kate Williams
                                               |8. Joe Williams""".stripMargin
  }

  it should "get list of employee names with letters" in new Fixture {

    exercises.listOfEmployeesNamesWithLetters shouldBe """|A. Joe Doe
                                                          |B. Bill Johnson
                                                          |C. Tony Lopez
                                                          |D. Gary Newman
                                                          |E. Jane Smith
                                                          |F. Ann Smith
                                                          |G. Kate Williams
                                                          |H. Joe Williams""".stripMargin
  }

  it should "lowest and highest salary" in new Fixture {

    exercises.minAndMaxSalary(CompanyRepository.employees) shouldBe Some((Salary(3000, "USD"), Salary(9100, "USD")))

    exercises.minAndMaxSalary(Nil) shouldBe None

  }

  it should "modify salaries" in new Fixture {

    exercises.modifySalaries(List(Salary(3000, "USD"), Salary(10000, "USD"), Salary(1000, "USD")), 1.2, 1.5) shouldBe List(
      Salary(1500, "USD"),
      Salary(3600, "USD"),
      Salary(12000, "USD")
    )

  }

  it should "get lowest and highest salary" in new Fixture {

    inside(exercises.twoBestEarningEmployees) {
      case Some((first, second)) =>
        first.salary.value shouldBe 9100
        second.salary.value shouldBe 9000

    }

  }

  it should "return true if employee is active" in new Fixture {

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

  it should "get all active employee" in new Fixture {

    exercises.getAllActiveEmployees.map(_.fullName) shouldBe List(
      "Jane Smith",
      "Bill Johnson",
      "Kate Williams",
      "Joe Williams",
      "Gary Newman",
      "Tony Lopez"
    )

  }

  it should "get active department of employee" in new Fixture {

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

  it should "get all employees email by domain" in new Fixture {

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

  it should "sum of managers salaries" in new Fixture {
    exercises.getSumOfTopLevelManagersSalaries() shouldBe 30100
  }

  it should "send email to all employees by department" in new Fixture {

    (actions.sendMail _).expects("jane.smith@acme.com", "hello")
    (actions.sendMail _).expects("bill.johnson@acme.com", "hello")
    (actions.sendMail _).expects("joe.williams@acme.com", "hello")
    (actions.sendMail _).expects("gary.newman@acme.com", "hello")
    (actions.sendMail _).expects("tony.lopez@acme.com", "hello")

    exercises.sendMailToAllEmployeesOfDepartment(2, "hello")

  }

  it should "split employees" in new Fixture {

    inside(exercises.splitEmployees) {
      case (moreThanOnePhone, moreThanOnePeriod, rest) =>
        moreThanOnePhone.map(_.fullName) shouldBe List("Jane Smith", "Bill Johnson", "Kate Williams", "Ann Smith")
        moreThanOnePeriod.map(_.fullName) shouldBe List("Joe Doe")
        rest.map(_.fullName) shouldBe List("Joe Williams", "Gary Newman", "Tony Lopez")
    }

  }

  it should "get office of employee" in new Fixture {

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

  it should "get pairs of all employees" in new Fixture {

    exercises.getPairsOfEmployees(List("Rick", "Brooke", "Terry")) shouldBe List(
      ("Rick", "Brooke"),
      ("Rick", "Terry"),
      ("Brooke", "Rick"),
      ("Brooke", "Terry"),
      ("Terry", "Rick"),
      ("Terry", "Brooke")
    )

  }

  it should "check if periods overlap" in new Fixture {

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

  it should "get sorted employees from newest" in new Fixture {

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

  it should "split employees on salary" in new Fixture {

    val (left, right) = exercises.aboveAndBelowSalary(4000)

    left.map(_.fullName) shouldBe List("Joe Doe", "Jane Smith", "Bill Johnson")
    right.map(_.fullName) shouldBe List("Kate Williams", "Ann Smith", "Joe Williams", "Gary Newman", "Tony Lopez")
  }

  it should "get employment days" in new Fixture {

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

  it should "return employees by departments" in new Fixture {

    exercises.employeesByDepartment.view.map {
      case (d, e) => d.name -> e.map(_.fullName)
    }.toMap shouldBe
      Map(
        "Engineering" -> List("Jane Smith", "Bill Johnson", "Joe Williams", "Gary Newman", "Tony Lopez"),
        "Security" -> List("Kate Williams")
      )
  }

  it should "return salaries by departments" in new Fixture {

    exercises.salariesByDepartment.view.map {
      case (d, s) => d.name -> s
    }.toMap shouldBe
      Map(
        "Engineering" -> 5720,
        "Security" -> 5000
      )
  }

  it should "get resources for user" in new Fixture {

    exercises.getAllAvailableResources(Admin("admin")) shouldBe List(
      EmployeeSalary(name = "developers' salaries"),
      EmployeeSalary(name = "CEO salary"),
      DatabasePassword(name = "main database password"),
      ProjectDocumentation(name = "project docs"),
      SecurityProcedure(name = "very important procedure"),
      SecurityReport(name = "public report", isSensitive = false),
      SecurityReport(name = "secret report", isSensitive = true)
    )

    exercises.getAllAvailableResources(RegularUser("user", 3)) shouldBe List(
      ProjectDocumentation("project docs"),
      SecurityProcedure("very important procedure"),
      SecurityReport("public report", false),
      SecurityReport("secret report", true)
    )

  }

  it should "get resources for employee" in new Fixture {

    val e = Employee(
      id = 1,
      firstName = "Tony",
      lastName = "Lopez",
      email = "tony.lopez@acme.com",
      employmentHistory = Vector.empty,
      phones = Nil,
      salary = Salary(100, "USD"),
      managerId = None,
      location = None
    )

    exercises.getAllAvailableResourcesForEmployee(e) shouldBe List(
      EmployeeSalary(name = "developers' salaries"),
      EmployeeSalary(name = "CEO salary"),
      DatabasePassword(name = "main database password"),
      ProjectDocumentation(name = "project docs"),
      SecurityProcedure(name = "very important procedure"),
      SecurityReport(name = "public report", isSensitive = false),
      SecurityReport(name = "secret report", isSensitive = true)
    )

  }

  it should "get highest ranking supervisor" in new Fixture {
    exercises.getHighestRankingSuperior(CompanyRepository.employees.find(_.id == 3).get).map(_.fullName) shouldBe Some("Joe Williams")
  }

}
