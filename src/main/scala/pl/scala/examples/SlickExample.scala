package pl.scala.examples

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._

object SlickExample extends App {

  //Slick requires configuration tables
  class CityTable(tag: Tag) extends Table[City](tag, "city") {
    def * = (id, name, countryId, population) <> (City.tupled, City.unapply)

    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")
    def countryId = column[Int]("country_id")
    def population = column[Option[Long]]("population")
  }

  class CountryTable(tag: Tag) extends Table[Country](tag, "country") {
    def * = (id, name) <> (Country.tupled, Country.unapply)

    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")
  }

  //Creating context
  def createDatabase() = {
    val pgDataSource = new org.postgresql.ds.PGSimpleDataSource()
    pgDataSource.setUser("postgres")
    Database.forDataSource(pgDataSource, None)
  }

  val cities = TableQuery[CityTable]
  val countries = TableQuery[CountryTable]

  val db = createDatabase()

  //simple query
  def getCityNamesStartingWith(s: String) = db.run(cities.map(_.name).filter(_.startsWith(s)).result)

  //simple query with join
  def getCitiesWithCountries = {
    val query = for {
      country <- countries
      city <- cities if country.id === city.countryId
    } yield (city.name, country.name)

    db.run(query.result)
  }

  //custom SQL
  def getCityName(id: Int) = db.run(sql"SELECT name FROM city WHERE id = $id".as[String])

  //TODO: Implement method for getting population of city by name
  def getPopulationOfCity(city: String) = ???

  //example of insert
  val insertTransaction = for {
    wroclaw <- cities.filter(_.name === "Wrocław").result.headOption
    _ <- if (wroclaw.isEmpty) cities += City(10, "Wrocław", 1, Some(500000)) else DBIO.successful(())
    plock <- cities.filter(_.name === "Płock").result.headOption
    _ <- if (plock.isEmpty) cities += City(11, "Płock", 1, Some(100000)) else DBIO.successful(())
    _ <- cities.filter(_.name === "Płock").map(_.population).update(Some(150000))
  } yield ()

  val program = for {
    citiesStartingWithW <- getCityNamesStartingWith("W")
    _ = println(s"Cities starting with W:$citiesStartingWithW")
    //populationOfBerlin <- getPopulationOfCity("Berlin")
    //_ = println(s"Population of Berlin:$populationOfBerlin")
    name <- getCityName(1)
    _ = println(s"City with id 1: $name")
    _ <- db.run(insertTransaction.transactionally)
    _ = println()
    _ = println("Cities with countries:")
    citiesAndCountries <- getCitiesWithCountries
    _ = citiesAndCountries.foreach {
      case (city, country) => println(s"$city: $country")
    }
  } yield ()

  Await.result(program, 10.seconds)

}
