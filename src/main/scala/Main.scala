import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.io.StdIn
import scala.concurrent.ExecutionContextExecutor

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // Define your routes here
  val route: Route =
    path("hello") {
      get {
        complete("Say hello to akka-http")
      }
    }

  // Bind the server to the instance’s public IP and port 8080
  val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(route)

  println(s"Server online at http://0.0.0.0:8080/\nPress RETURN to stop...")

  StdIn.readLine() // Let the server run until you hit return
  bindingFuture
    .flatMap(_.unbind()) // Unbind from the port
    .onComplete(_ => system.terminate()) // Shut down the system
}