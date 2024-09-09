name := "givery-rest-api"

version := "0.1"

scalaVersion := "2.13.10"

// Akka HTTP and Streams
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.2.9",
  "com.typesafe.akka" %% "akka-stream" % "2.6.19",
  "de.heikoseeberger" %% "akka-http-circe" % "1.38.2", // Circe for Akka HTTP
  "io.circe" %% "circe-generic" % "0.14.1",           // Circe JSON support
  "io.circe" %% "circe-parser" % "0.14.1",            // Circe JSON parsing
  "com.typesafe.slick" %% "slick" % "3.4.0",          // Slick core
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.0", // Slick HikariCP connection pool
  "mysql" % "mysql-connector-java" % "8.0.28",        // MySQL JDBC driver
  "com.zaxxer" % "HikariCP" % "4.0.3",                 // HikariCP itself
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "ch.megard" %% "akka-http-cors" % "1.1.3"
)
