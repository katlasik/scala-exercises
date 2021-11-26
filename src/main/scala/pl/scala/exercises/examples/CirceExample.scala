package pl.scala.exercises.examples

import io.circe.Decoder.Result
import io.circe._
import io.circe.generic.semiauto._
import io.circe.parser._
import io.circe.syntax._

case class Car(name: String, isHybrid: Boolean)

object Car {

  implicit val decoder: Decoder[Car] = new Decoder[Car] {

    override def apply(c: HCursor): Result[Car] = for {
      name <- c.get[String]("name")
      isHybrid <- c.get[Boolean]("isHybrid")
    } yield Car(name, isHybrid)
  }

  //implicit val decoder: Decoder[Car] = deriveDecoder

  implicit val encoder: Encoder[Car] = new Encoder[Car] {

    override def apply(c: Car): Json = Json.obj(
      ("name", Json.fromString(c.name)),
      ("isHybrid", Json.fromBoolean(c.isHybrid))
    )
  }

  //implicit val encoder: Encoder[Car] = deriveEncoder

}

object CirceExample extends App {

  val json =
    """
      {
        "isHybrid": true,
        "name": "Toyota Rav4"
      }
      """

  val decoded: Either[Error, Car] = parse(json).flatMap(_.as[Car])

  println(s"Decoded: $decoded")

  val encoded = Car(name = "Ford Kuga", isHybrid = false).asJson.noSpaces

  println(s"Encoded: $encoded")
}
