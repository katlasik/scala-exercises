package pl.scala.exercises.stdlib

import pl.scala.exercises.model._

import java.time.LocalDate

private[stdlib] object CompanyRepository {

  private val now = LocalDate.now()

  val employees: List[Employee] = List(
    Employee(
      id = 1,
      firstName = "Joe",
      lastName = "Doe",
      email = "joe.doe@acme.com",
      employmentHistory = Vector(
        EmploymentPeriod(
          from = now.minusYears(10),
          to = Some(now.minusYears(4)),
          departmentId = 1
        ),
        EmploymentPeriod(
          from = now.minusYears(3).plusDays(1),
          to = Some(now.minusYears(2)),
          departmentId = 2
        )
      ),
      phones = Nil,
      managerId = Some(6),
      salary = Salary(3000, "USD")
    ),
    Employee(
      id = 2,
      firstName = "Jane",
      lastName = "Smith",
      email = "jane.smith@acme.com",
      employmentHistory = Vector(
        EmploymentPeriod(
          from = now.minusYears(4).plusDays(10),
          to = Some(now.minusYears(2).plusMonths(3)),
          departmentId = 1
        ),
        EmploymentPeriod(
          from = now.minusYears(2).plusMonths(3).plusDays(1),
          to = None,
          departmentId = 2
        ),
        EmploymentPeriod(
          from = now.minusYears(5),
          to = Some(now.minusYears(4).plusDays(9)),
          departmentId = 2
        )
      ),
      phones = List("444", "555234123"),
      managerId = Some(1),
      salary = Salary(3000, "USD")
    ),
    Employee(
      id = 3,
      firstName = "Bill",
      lastName = "Johnson",
      email = "bill.johnson@acme.com",
      employmentHistory = Vector(
        EmploymentPeriod(
          from = now.minusDays(5),
          to = None,
          departmentId = 2
        )
      ),
      phones = List("421", "785123123", "444333123"),
      managerId = Some(5),
      salary = Salary(3500, "USD")
    ),
    Employee(
      id = 4,
      firstName = "Kate",
      lastName = "Williams",
      email = "kate.williams@acme.com",
      employmentHistory = Vector(
        EmploymentPeriod(
          from = now.minusYears(2).plusMonths(3).plusDays(1),
          to = None,
          departmentId = 3
        )
      ),
      phones = List("342", "111222333"),
      salary = Salary(5000, "USD")
    ),
    Employee(
      id = 4,
      firstName = "Ann",
      lastName = "Smith",
      email = "ann.smith@external.com",
      employmentHistory = Vector.empty,
      phones = List("342", "111222333"),
      salary = Salary(7000, "USD")
    ),
    Employee(
      id = 5,
      firstName = "Joe",
      lastName = "Williams",
      email = "joe.williams@acme.com",
      employmentHistory = Vector(
        EmploymentPeriod(
          from = now.minusYears(4).plusMonths(2).plusDays(1),
          to = None,
          departmentId = 2
        )
      ),
      phones = List("942"),
      salary = Salary(9000, "USD")
    ),
    Employee(
      id = 6,
      firstName = "Gary",
      lastName = "Newman",
      email = "gary.newman@acme.com",
      employmentHistory = Vector(
        EmploymentPeriod(
          from = now.minusYears(14).plusMonths(5).plusDays(1),
          to = None,
          departmentId = 2
        )
      ),
      phones = List("884343123"),
      managerId = Some(7),
      salary = Salary(4000, "USD")
    ),
    Employee(
      id = 7,
      firstName = "Tony",
      lastName = "Lopez",
      email = "tony.lopez@acme.com",
      employmentHistory = Vector(
        EmploymentPeriod(
          from = now.minusYears(15).plusMonths(1).plusDays(10),
          to = None,
          departmentId = 2
        )
      ),
      phones = List("884343123"),
      salary = Salary(9100, "USD")
    )
  )

  val departments: List[Department] = List(
    Department(id = 1, name = "Accounting"),
    Department(id = 2, name = "Engineering"),
    Department(id = 3, name = "Security")
  )

  val resources: List[CompanyResource] = List(
    EmployeeSalary(name = "developers' salaries"),
    EmployeeSalary(name = "CEO salary"),
    DatabasePassword(name = "main database password"),
    ProjectDocumentation(name = "project docs"),
    SecurityProcedure(name = "very important procedure"),
    SecurityReport(name = "public report", isSensitive = false),
    SecurityReport(name = "secret report", isSensitive = true)
  )

}
