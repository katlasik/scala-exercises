package pl.scala.exercises.sttp

import sttp.client3._
import sttp.client3.akkahttp._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

/**
 * Implement methods in PostApp class to get posts and users from urls in Urls object.
 * Display list of posts' titles and users in two columns:
 *
 * user1  title1
 * user2  title2
 * .....
 */

object Urls {
  val Posts = uri"https://jsonplaceholder.typicode.com/posts"
  val Users = uri"https://jsonplaceholder.typicode.com/users"
}

case class Post()
case class User()


object PostApp extends App {

  def getUsers(): Future[List[User]] = ???
  def getPosts(): Future[List[Post]] = ???
  def display(): Future[Unit] = ???

  val backend: SttpBackend[Future, Any] = AkkaHttpBackend()

  Await.result(display(), 30.seconds)


}
