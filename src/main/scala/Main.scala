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
import scala.io.StdIn
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

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
  val route: Route = cors() {
    get{
      path(""){
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }
    //path("") {
      //get {
        // Basic testing route
        //complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        // Uncomment the following to test database retrieval:
        // onSuccess(recipeRepository.getAllRecipes) { recipes =>
        //   complete(recipes)  // Responding with JSON formatted recipes
        // }
      //}
      // Uncomment this block to test recipe insertion:
      // post {
      //   entity(as[Recipe]) { recipe =>
      //     onSuccess(recipeRepository.insertRecipe(recipe)) { id =>
      //       complete(s"Recipe added with ID: $id")
      //     }
      //   }
      // }
      //}
    }
    // Uncomment this block to test retrieval, update, and deletion of recipes by ID:
    // path("recipes" / IntNumber) { id =>
    //   get {
    //     onSuccess(recipeRepository.getRecipeById(id)) {
    //       case Some(recipe) => complete(recipe)
    //       case None => complete(s"Recipe with ID $id not found")
    //     }
    //   } ~
    //   put {
    //     entity(as[Recipe]) { updatedRecipe =>
    //       onSuccess(recipeRepository.updateRecipe(id, updatedRecipe)) { _ =>
    //         complete(s"Recipe with ID $id updated")
    //       }
    //     }
    //   } ~
    //   delete {
    //     onSuccess(recipeRepository.deleteRecipe(id)) { _ =>
    //       complete(s"Recipe with ID $id deleted")
    //     }
    //   }
    // }

  // Bind and run the server on the specified IP and port
  val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)


  println(s"Server online at http://0.0.0.0:8080/\nPress RETURN to stop...")
  StdIn.readLine() // Press ENTER to stop the server

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
