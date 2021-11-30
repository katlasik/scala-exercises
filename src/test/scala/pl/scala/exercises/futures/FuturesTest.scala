package pl.scala.exercises.futures

import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pl.scala.exercises.model.{Employee, Salary}

import scala.concurrent.Future

class FuturesTest extends AnyFlatSpec with MockFactory with GivenWhenThen with ScalaFutures with Matchers with Eventually {

  trait Fixture {

    val actions = mock[Actions]
    val futures = new Futures(actions)

  }

  it should "get manager of employee" in new Fixture {

    Given("employee has a manager")

    val manager = Employee(
      id = 2,
      firstName = "Joe",
      lastName = "Doe",
      email = "0",
      employmentHistory = Vector.empty,
      phones = Nil,
      salary = Salary(0, "PLN"),
      managerId = None,
      location = None
    )

    val employee = Employee(
      id = 1,
      firstName = "Joe",
      lastName = "Doe",
      email = "0",
      employmentHistory = Vector.empty,
      phones = Nil,
      salary = Salary(0, "PLN"),
      managerId = Some(manager.id),
      location = None
    )

    (actions.getEmployee _).expects(1).returns(
      Future.successful(employee)
    )

    (actions.getEmployee _).expects(2).returns(
      Future.successful(manager)
    )

    When("get manager function is called")
    val future = futures.getEmployeesManager(1)

    Then("it should return manager")
    val result = future.futureValue

    result shouldBe manager

  }

  it should "return failed future when employee has no manager" in new Fixture {

    Given("employee has a manager")

    val employee = Employee(
      id = 1,
      firstName = "Joe",
      lastName = "Doe",
      email = "0",
      employmentHistory = Vector.empty,
      phones = Nil,
      salary = Salary(0, "PLN"),
      managerId = None,
      location = None
    )

    (actions.getEmployee _).expects(1).returns(
      Future.successful(employee)
    )

    When("get manager function is called")
    val future = futures.getEmployeesManager(1)

    Then("it should return manager")
    eventually {
      future.eitherValue shouldBe Some(Left(NoManager))
    }
  }

  it should "return sum of salaries" in new Fixture {
    // Implement
  }

  it should "get employee and department" in new Fixture {
    // Implement
  }

  it should "retry giveRaise 3 times" in new Fixture {

    Given("employee")

    val employee = Employee(
      id = 1,
      firstName = "Joe",
      lastName = "Doe",
      email = "0",
      employmentHistory = Vector.empty,
      phones = Nil,
      salary = Salary(0, "PLN"),
      managerId = None,
      location = None
    )

    val newSalary = Salary(100, "PLN")

    (actions.getEmployee _).expects(1).returns(Future.successful(employee)).anyNumberOfTimes()

    (actions.updateEmployee _).expects(employee.copy(salary = newSalary)).returns(Future.failed(ServerNotAvailable)).repeat(4)

    When("Server fails")
    val future = futures.giveRaise(1, newSalary)

    Then("Update is attempted 3 times")
    eventually {
      future.eitherValue shouldBe Some(Left(ServerNotAvailable))
    }
  }

  it should "return false if funds are too low" in new Fixture {

    Given("employee")

    val employee = Employee(
      id = 1,
      firstName = "Joe",
      lastName = "Doe",
      email = "0",
      employmentHistory = Vector.empty,
      phones = Nil,
      salary = Salary(0, "PLN"),
      managerId = None,
      location = None
    )

    val newSalary = Salary(100, "PLN")

    (actions.getEmployee _).expects(1).returns(Future.successful(employee))

    (actions.updateEmployee _).expects(employee.copy(salary = newSalary)).returns(Future.failed(NotEnoughFunds)).once()

    When("Server fails")
    val future = futures.giveRaise(1, newSalary)

    Then("false is returned")
    future.futureValue shouldBe false
  }

  it should "return true if update is successful" in new Fixture {

    Given("employee")

    val employee = Employee(
      id = 1,
      firstName = "Joe",
      lastName = "Doe",
      email = "0",
      employmentHistory = Vector.empty,
      phones = Nil,
      salary = Salary(0, "PLN"),
      managerId = None,
      location = None
    )

    val newSalary = Salary(100, "PLN")

    (actions.getEmployee _).expects(1).returns(Future.successful(employee))

    (actions.updateEmployee _).expects(employee.copy(salary = newSalary)).returns(Future.unit).once()

    When("Server fails")
    val future = futures.giveRaise(1, newSalary)

    Then("false is returned")
    future.futureValue shouldBe true
  }

  it should "return error if new salary is smaller than previous" in new Fixture {

    Given("employee")

    val employee = Employee(
      id = 1,
      firstName = "Joe",
      lastName = "Doe",
      email = "0",
      employmentHistory = Vector.empty,
      phones = Nil,
      salary = Salary(200, "PLN"),
      managerId = None,
      location = None
    )

    val newSalary = Salary(100, "PLN")

    (actions.getEmployee _).expects(1).returns(Future.successful(employee))

    (actions.updateEmployee _).expects(*).returns(Future.unit).never()

    When("Server fails")
    val future = futures.giveRaise(1, newSalary)

    Then("false is returned")
    eventually {
      future.eitherValue shouldBe Some(Left(SalaryTooLow))
    }
  }

}
