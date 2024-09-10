name := "givery-rest-api"

version := "0.1"

scalaVersion := "2.13.10" // Ensure this matches the rest of your project

// Akka HTTP and Streams
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.2.9",        // Latest compatible Akka HTTP version
  "com.typesafe.akka" %% "akka-stream" % "2.6.19",      // Akka Stream for HTTP
  "de.heikoseeberger" %% "akka-http-circe" % "1.38.2",  // Circe integration for Akka HTTP
  "io.circe" %% "circe-generic" % "0.14.1",             // Circe for JSON support
  "io.circe" %% "circe-parser" % "0.14.1",              // Circe for JSON parsing
  "com.typesafe.slick" %% "slick" % "3.4.0",            // Slick ORM
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.0",   // Slick HikariCP connection pool
  "mysql" % "mysql-connector-java" % "8.0.28",          // MySQL JDBC driver
  "com.zaxxer" % "HikariCP" % "4.0.3",                  // HikariCP for connection pooling
  "ch.qos.logback" % "logback-classic" % "1.2.3",       // Logback for logging
  "ch.megard" %% "akka-http-cors" % "1.1.3"             // CORS support for Akka HTTP
)

// Enable strict settings for compatibility with Scala 2.13.x
scalacOptions ++= Seq("-deprecation", "-feature")

// Manually specify the Main class
mainClass in Compile := Some("Main")

// If Main is in a package, change "Main" to your fully qualified class name, for example:
// mainClass in Compile := Some("com.yourpackage.Main")
