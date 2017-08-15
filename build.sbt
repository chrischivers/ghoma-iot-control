
name := "ghoma-iot-control"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.9"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.3.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.0"
libraryDependencies += "com.typesafe" % "config" % "1.3.1"

lazy val commonSettings = Seq(
  version := "1.0",
  organization := "com.ghoma.control",
  scalaVersion := "2.12.2",
  test in assembly := {}
)

lazy val app = (project in file("app")).
  settings(commonSettings: _*).
  settings(
    mainClass in assembly := Some("com.ghoma.control.Main"),
    test in assembly := {}
  )