import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import models._
import repositories._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.ByteString
import play.api.libs.json._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import java.sql.Timestamp
import slick.jdbc.MySQLProfile.api._
import java.text.SimpleDateFormat

// Case class for RecipeData
case class RecipeData(title: String, makingTime: String, serves: String, ingredients: String, cost: Int)

// Companion object for JSON formatting using Play JSON
object RecipeData {
  implicit val recipeDataFormat: OFormat[RecipeData] = Json.format[RecipeData]
}
trait JsonFormats {
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    private val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    def writes(ts: Timestamp): JsValue = JsString(dateFormat.format(ts))

    def reads(json: JsValue): JsResult[Timestamp] = json match {
      case JsString(s) => 
        try {
          JsSuccess(new Timestamp(dateFormat.parse(s).getTime))
        } catch {
          case _: Exception => JsError("Invalid date format")
        }
      case _ => JsError("String expected")
    }
  }
  implicit val recipeFormat: OFormat[Recipe] = Json.format[Recipe]
}
// Custom Play JSON Marshallers and Unmarshallers
object PlayJsonSupport {
  import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
  import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}

  // Unmarshaller: Convert JSON to RecipeData
  implicit def playJsonUnmarshaller[T](implicit reads: Reads[T]): FromEntityUnmarshaller[T] =
    Unmarshaller.byteStringUnmarshaller.forContentTypes(ContentTypes.`application/json`).map { data =>
      Json.parse(data.utf8String).as[T]
    }

  // Marshaller: Convert RecipeData to JSON
  implicit def playJsonMarshaller[T](implicit writes: Writes[T]): ToEntityMarshaller[T] =
    Marshaller.withFixedContentType(ContentTypes.`application/json`) { recipe =>
      val json = Json.toJson(recipe)
      HttpEntity(ContentTypes.`application/json`, ByteString(json.toString()))
    }
}

object Main extends App with JsonFormats {

  implicit val system = ActorSystem("my-system")
  implicit val executionContext = system.dispatcher

  // Sample data for testing
  val db = Database.forConfig("mydb")
  val recipeRepository = new RecipeRepository(db)

  import PlayJsonSupport._

  // Route definition
  val route: Route =
    path("recipes" / IntNumber) { id =>
      get {
        onSuccess(recipeRepository.getRecipeById(id)) {
          case Some(recipe) => {
              val jsonResponse = Json.obj(
                "message" -> JsString("Recipe details by id"),
                "recipes" -> Json.toJson(recipe)
              )
              complete(HttpEntity(ContentTypes.`application/json`, jsonResponse.toString()))
          }
          case None => complete(s"Recipe with ID $id not found")
        }
      } ~
      patch {
        entity(as[RecipeData]) { recipeData =>
          val updateRecipe = Recipe(None, recipeData.title, recipeData.makingTime, recipeData.serves, recipeData.ingredients, recipeData.cost)
          onComplete(recipeRepository.updateRecipe(id, updateRecipe)) { 
            case scala.util.Success(id) =>
              val jsonResponse = Json.obj(
                "message" -> JsString("Recipe successfully updated!"),
                "recipe" -> Json.toJson(updateRecipe)
              )
              complete(HttpEntity(ContentTypes.`application/json`, jsonResponse.toString()))
            case scala.util.Failure(exception) =>
              val jsonResponse = Json.obj("message" -> JsString("No recipe found!"))
              complete(HttpEntity(ContentTypes.`application/json`, jsonResponse.toString()))
          }
        }
      } ~
      delete {
        onComplete(recipeRepository.deleteRecipe(id)) {
          case scala.util.Success(id) =>
            val jsonResponse = Json.obj("message" -> JsString("Recipe successfully removed!"))
            complete(HttpEntity(ContentTypes.`application/json`, jsonResponse.toString()))
          case scala.util.Failure(exception) =>
            val jsonResponse = Json.obj("message" -> JsString("No recipe found!"))
            complete(HttpEntity(ContentTypes.`application/json`, jsonResponse.toString()))
        }
      }
    } ~
    path("recipes") {
      get {
        onSuccess(recipeRepository.getAllRecipes) { recipes =>
          val jsonResponse = Json.obj("recipes" -> Json.toJson(recipes))
          complete(HttpEntity(ContentTypes.`application/json`, jsonResponse.toString()))
        }
      } ~
      post {
        entity(as[RecipeData]) { recipeData =>
          val currentTimestamp = new Timestamp(System.currentTimeMillis())
          val newRecipe = Recipe(None, recipeData.title, recipeData.makingTime, recipeData.serves, recipeData.ingredients, recipeData.cost, Some(currentTimestamp), Some(currentTimestamp))
          onComplete(recipeRepository.insertRecipe(newRecipe)) {
            case scala.util.Success(id) =>
              val savedRecipe = newRecipe.copy(id = Some(id))
              val jsonResponse = Json.obj(
                "message" -> JsString("Recipe successfully created!"),
                "recipe" -> Json.toJson(savedRecipe)
              )
              complete(HttpEntity(ContentTypes.`application/json`, jsonResponse.toString()))

            case scala.util.Failure(exception) =>
              val errorResponse = Json.obj(
                "message" -> JsString("Recipe creation failed!"),
                "required" -> JsString("title, makingTime, serves, ingredients, cost"),
              )
              complete(StatusCodes.InternalServerError, HttpEntity(ContentTypes.`application/json`, errorResponse.toString()))
          }
        }
      }
    } ~
    complete(StatusCodes.NotFound, "The requested resource could not be found.")
    

  // Start the server
  val port = sys.env.getOrElse("PORT", "8080").toInt
  val bindingFuture = Http().newServerAt("0.0.0.0", port).bind(route)

  println(s"Server online at http://localhost:$port/")

  Await.result(system.whenTerminated, Duration.Inf)
}
