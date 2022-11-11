package pl.scala.exercises.stdlib

private[stdlib] trait Actions {
  def sendMail(email: String, message: String): Unit
}
