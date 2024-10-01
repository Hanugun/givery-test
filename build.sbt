name := "givery-rest-api"

version := "0.1"

scalaVersion := "2.13.10"

// Akka HTTP and Streams
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.6.19",
  "com.typesafe.akka" %% "akka-http" % "10.2.9",
  "com.typesafe.akka" %% "akka-stream" % "2.6.19",
  "de.heikoseeberger" %% "akka-http-circe" % "1.38.2",
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.9",
  "com.typesafe.slick" %% "slick" % "3.4.0",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.0",
  "mysql" % "mysql-connector-java" % "8.0.28",
  "com.zaxxer" % "HikariCP" % "4.0.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.2.9" % Test,
  "org.scalatest" %% "scalatest" % "3.2.9" % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.16" % Test,
  "ch.megard" %% "akka-http-cors" % "1.1.3"
)

// Enable strict settings for compatibility with Scala 2.13.x
scalacOptions ++= Seq("-deprecation", "-feature")

// Manually specify the Main class using the new slash syntax
Compile / mainClass := Some("Main") // Update "Main" with your actual class if necessary

// sbt-native-packager plugin for Heroku deployment
enablePlugins(JavaAppPackaging)
