package pl.scala.examples

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

object QuillExample extends App {
  import io.getquill._

  //Creating context
  def createContext: PostgresJdbcContext[SnakeCase] = {
    val pgDataSource = new org.postgresql.ds.PGSimpleDataSource()
    pgDataSource.setUser("postgres")
    val config = new HikariConfig()
    config.setDataSource(pgDataSource)
    new PostgresJdbcContext(SnakeCase, new HikariDataSource(config))
  }

  val ctx = createContext

  import ctx._

  //simple query
  def getCityNamesStartingWith(s: String) =
    ctx.run(
      quote {
        query[City].map(_.name).filter(_.startsWith(lift(s)))
      }
    )

  //simple query with join
  def getCitiesWithCountries = ctx.run(
    quote {
      query[City].join(query[Country])
        .on {
          case (city, country) => city.countryId == country.id
        }.map {
          case (city, country) => (city.name, country.name)
        }
    }
  )

  //cutoms SQL
  def getCityName(id: Int) = ctx.run(quote(sql"SELECT name FROM city WHERE id = ${lift(id)}".as[String]))

  //TODO: Implement method for getting population of city by name
  def getPopulationOfCity(city: String) = ???

  //Example of insert
  def insertCity(city: City) = quote(query[City].insertValue(lift(city)).onConflictIgnore)

  println(s"Cities starting with W: ${getCityNamesStartingWith("W")}")
  //println(s"Population of Berlin:${getPopulationOfCity("Berlin")}")

  println(s"City with id 1: ${getCityName(1)}")

  ctx.transaction {
    insertCity(City(10, "Poznan", 1, Some(500000)))
    insertCity(City(11, "Plock", 1, Some(100000)))
    quote(query[City].map(_.population).updateValue(lift(Some(150000))))
  }

  println()
  println("Cities with countries:")
  getCitiesWithCountries.foreach {
    case (city, country) => println(s"$city: $country")
  }

}
