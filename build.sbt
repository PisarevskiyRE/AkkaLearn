ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "AkkaLearn"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.8.0",
  "com.typesafe.akka" %% "akka-http-core" % "10.5.0",
  "com.typesafe.akka" %% "akka-slf4j" % "2.8.0",
  "com.typesafe.akka" %% "akka-stream" % "2.8.0",
  "ch.qos.logback" % "logback-classic" % "1.4.6",
  "com.typesafe.akka" %% "akka-testkit" % "2.8.0" % "test",
  "org.scalatest" %% "scalatest" % "3.2.15" % "test",
  "org.scalameta" %% "munit" % "0.7.29" % "test",
  "joda-time" % "joda-time" % "2.12.5",
   "com.github.nscala-time" %% "nscala-time" % "2.32.0"
)