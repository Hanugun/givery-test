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
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.json._ 
import java.sql.Timestamp 
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._
trait JsonFormats {
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(ts: Timestamp): JsValue = JsNumber(ts.getTime)
    def reads(json: JsValue): JsResult[Timestamp] = json match {
      case JsNumber(t) => JsSuccess(new Timestamp(t.toLong))
      case _ => JsError("Timestamp expected")
    }
  }

  implicit val recipeDataFormat: OFormat[RecipeData] = Json.format[RecipeData]
  implicit val recipeFormat: OFormat[Recipe] = Json.format[Recipe]
}

object Main extends App with JsonFormats{
  
  implicit val system = ActorSystem("my-system")

  val db = Database.forConfig("mydb")
  val recipeRepository = new RecipeRepository(db)

  val route: Route =
    path("") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    } ~
    path("recipes") {
      get {
        onSuccess(recipeRepository.getAllRecipes) { recipes =>
          val jsonResponse = Json.obj("recipes" -> Json.toJson(recipes))
          complete(HttpEntity(ContentTypes.`application/json`, jsonResponse.toString()))
        }
      } 
      // ~
      // post {
      //   entity(as[RecipeData]) { recipeData => 
      //     val newRecipe = Recipe(None, recipeData.title, recipeData.makingTime, recipeData.serves, recipeData.ingredients, recipeData.cost)
          
      //     onSuccess(recipeRepository.insertRecipe(newRecipe)) { id =>
      //       val savedRecipe = newRecipe.copy(id = Some(id))
            
      //       val jsonResponse = Json.obj(
      //         "message" -> JsString("Recipe successfully added"),
      //         "recipe" -> Json.toJson(savedRecipe)
      //       )
            
      //       complete(HttpEntity(ContentTypes.`application/json`, jsonResponse.toString()))
      //     }
      //   }
      // }
    }

  val port = sys.env.getOrElse("PORT", "8080").toInt
  val bindingFuture = Http().newServerAt("0.0.0.0", port).bind(route)

  println(s"Server online at http://0.0.0.0:$port/")

  Await.result(system.whenTerminated, Duration.Inf)
}
