import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import models._
import repositories._
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import scala.concurrent.Await
import scala.concurrent.duration._

// Import Circe for JSON marshalling/unmarshalling
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

object Main extends App {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()

  // Initialize database and repository
  val db = Database.forConfig("mydb")
  val recipeRepository = new RecipeRepository(db)

  // Define routes for basic and CRUD operations on recipes
  val route: Route =
    get {
      path("") {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }

  // Bind and run the server on the specified IP and port
  val port = sys.env.getOrElse("PORT", "8080").toInt // Heroku will provide PORT, default to 8080 if running locally
  val bindingFuture = Http().newServerAt("0.0.0.0", port).bind(route)

  println(s"Server online at http://0.0.0.0:$port/")

  // Keep the server running
  Await.result(system.whenTerminated, Duration.Inf)
}
