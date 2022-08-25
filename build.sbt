name := "scala-exercises"

version := "0.1"

scalaVersion := "2.13.7"

val AkkaVersion = "2.6.19"
val CirceVersion = "0.14.2"
val EnumeratumVersion = "1.7.0"
val SttpVersion = "3.7.2"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % CirceVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-parser" % CirceVersion,
  "com.beachape" %% "enumeratum" % EnumeratumVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "com.softwaremill.sttp.client3" %% "core" % SttpVersion,
  "com.softwaremill.sttp.client3" %% "circe" % SttpVersion,
  "com.softwaremill.sttp.client3" %% "akka-http-backend" % SttpVersion,
  "de.heikoseeberger" %% "akka-http-circe" % "1.39.2",
  "com.typesafe.akka" %% "akka-stream" % "2.6.19",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.19",
  "com.lightbend.akka" %% "akka-stream-alpakka-file" % "3.0.4",
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.0",
  "io.getquill" %% "quill-jdbc" % "4.3.0",
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "org.apache.kafka" % "kafka-clients" % "3.2.1",
  "org.postgresql" % "postgresql" % "42.4.2",
  "com.typesafe.akka" %% "akka-http" % "10.2.9",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.2.9" % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.12" % Test,
  "org.scalamock" %% "scalamock" % "5.2.0" % Test
)

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-Dquill.macro.log=false")
