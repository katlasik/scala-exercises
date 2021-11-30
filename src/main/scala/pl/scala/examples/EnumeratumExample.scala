package pl.scala.examples

import enumeratum._

sealed trait Greeting extends EnumEntry

object Greeting extends Enum[Greeting] {

  val values = findValues

  case object Hello extends Greeting
  case object GoodBye extends Greeting
  case object Hi extends Greeting
  case object Bye extends Greeting


  def polishVersion(g: Greeting): String = g match {
    case Hello | Hi => "Cześć"
    case GoodBye => "Dobranoc"
   }
}
