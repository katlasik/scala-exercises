name := "scala-exercises"

version := "0.1"

scalaVersion := "2.13.7"

libraryDependencies += "io.circe" %% "circe-core" % "0.14.1"
libraryDependencies += "io.circe" %% "circe-generic" % "0.14.1"
libraryDependencies += "io.circe" %% "circe-parser" % "0.14.1"
libraryDependencies += "com.beachape" %% "enumeratum" % "1.7.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "5.1.0" % Test

scalacOptions := Seq("-unchecked", "-deprecation", "-feature")
