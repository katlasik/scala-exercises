package pl.scala.exercises.akka

import scala.concurrent.Future

trait PrizeService {
  def orderPrize(name: String): Future[Unit]
}

case object DummyPrizeService extends PrizeService {

  override def orderPrize(name: String): Future[Unit] = name match {
    case "watch" => Future.successful(())
    case e => Future.failed(new IllegalStateException(s"Item $e out of stock!"))
  }
}
