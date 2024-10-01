import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import akka.http.scaladsl.model.StatusCodes
import play.api.libs.json.Json

class MainSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  // Assuming the `Main.route` is the main route object under test
  val routes = Main.route

  "Recipe API" should {

    "return recipe details by id" in {
      // Mock behavior for repository to return a sample recipe.
      Get("/recipes/1") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual `application/json`
        val responseJson = responseAs[String]
        responseJson should include ("Recipe details by id")
      }
    }

    "create a new recipe" in {
      val recipeDataJson = Json.obj(
        "title" -> "Test Recipe",
        "making_time" -> "30 minutes",
        "serves" -> "4",
        "ingredients" -> "Test ingredients",
        "cost" -> 500
      )
      Post("/recipes", recipeDataJson) ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] should include ("Recipe successfully created!")
      }
    }

    "update an existing recipe" in {
      val updateDataJson = Json.obj(
        "title" -> "Updated Recipe",
        "making_time" -> "45 minutes",
        "serves" -> "6",
        "ingredients" -> "Updated ingredients",
        "cost" -> 600
      )
      Patch("/recipes/1", updateDataJson) ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] should include ("Recipe successfully updated!")
      }
    }

    "return not found when recipe does not exist" in {
      Get("/recipes/9999") ~> routes ~> check {
        status shouldEqual StatusCodes.NotFound
        responseAs[String] should include ("No recipe found!")
      }
    }

    "delete an existing recipe" in {
      Delete("/recipes/1") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] should include ("Recipe successfully removed!")
      }
    }
  }
}
