package pl.scala.exercises.akka

case class Points(value: Int) extends AnyVal {
  def +(p: Points): Points = Points(value + p.value)
  def -(p: Points): Points = Points(value - p.value)
}

object Points {
  implicit val ordering: Ordering[Points] = Ordering.by(_.value)
}
