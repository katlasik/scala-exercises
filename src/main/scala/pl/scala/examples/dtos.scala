package pl.scala.examples

case class City(id: Int, name: String, countryId: Int, population: Option[Long])
case class Country(id: Int, name: String)