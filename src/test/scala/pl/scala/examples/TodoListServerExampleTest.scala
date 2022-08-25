package pl.scala.examples

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class TodoListServerExampleTest extends AnyFlatSpec with Matchers with ScalatestRouteTest with MockFactory {

  trait Fixture {
    val service = mock[ToDoService]

    val routes = TodoListServerExample.route(service)
  }

  it should "get messages" in new Fixture {

    (service.listItems _).expects().returns(Future.successful(List(TodoItem(1, "Pij wodę", false))))

    Get("/items") ~> routes ~> check {
      responseAs[String] shouldEqual """[{"id":1,"text":"Pij wodę","done":false}]"""
    }
  }

  it should "add messages" in new Fixture {

    (service.add _).expects("Kup mleko").returns(Future.successful(TodoItem(1, "Kup mleko", false)))

    val payload = """{
                       "text": "Kup mleko"
                   }"""

    val request = HttpRequest(
      HttpMethods.POST,
      uri = "/items",
      entity = HttpEntity(MediaTypes.`application/json`, payload)
    )

    request ~> routes ~> check {
      responseAs[String] shouldEqual """{"id":1,"text":"Kup mleko","done":false}"""
    }
  }

}
