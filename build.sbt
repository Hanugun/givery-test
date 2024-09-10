name := "givery-rest-api"

version := "0.1"

scalaVersion := "2.13.10"

// Akka HTTP and Streams
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.2.9",
  "com.typesafe.akka" %% "akka-stream" % "2.6.19",
  "de.heikoseeberger" %% "akka-http-circe" % "1.38.2",
  "io.circe" %% "circe-generic" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1",
  "com.typesafe.slick" %% "slick" % "3.4.0",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.0",
  "mysql" % "mysql-connector-java" % "8.0.28",
  "com.zaxxer" % "HikariCP" % "4.0.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "ch.megard" %% "akka-http-cors" % "1.1.3"
)

// Enable strict settings for compatibility with Scala 2.13.x
scalacOptions ++= Seq("-deprecation", "-feature")

// Manually specify the Main class using the new slash syntax
Compile / mainClass := Some("Main") // Update "Main" with your actual class if necessary

// sbt-native-packager plugin for Heroku deployment
enablePlugins(JavaAppPackaging)
