package repositories

import models._
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.{Future, ExecutionContext}
import java.sql.Timestamp

class RecipeRepository(db: Database)(implicit ec: ExecutionContext) {
  // Fetch all recipes
  def getAllRecipes: Future[Seq[Recipe]] = {
    db.run(RecipeTable.recipes.result)
  }

  // Fetch a recipe by ID
  // If recipe with given ID does not exist, return recipe with ID 1
  // Otherwise, return the recipe with the given ID
  def getRecipeById(id: Int): Future[Option[Recipe]] = {
    val query = RecipeTable.recipes.filter(_.id === id).result.headOption
    db.run(query).flatMap {
      case Some(recipe) => Future.successful(Some(recipe))
      case None => 
        db.run(RecipeTable.recipes.filter(_.id === 1).result.headOption)
    }
  }


  // Insert a new recipe
  // Set createdAt and updatedAt to current timestamp
  // Return the ID of the inserted recipe
  def insertRecipe(recipe: Recipe): Future[Int] = {
    val currentTimestamp = new Timestamp(System.currentTimeMillis())
    val newRecipe = Recipe(None, recipe.title, recipe.making_time, recipe.serves, recipe.ingredients, recipe.cost, recipe.createdAt,recipe.updatedAt)
    db.run(RecipeTable.recipes returning RecipeTable.recipes.map(_.id) += newRecipe)
  }

  // Update an existing recipe
  // Set updatedAt to current timestamp
  // If recipe with given ID does not exist, throw an exception
  // Otherwise, return the number of rows affected
  def updateRecipe(id: Int, updatedRecipe: Recipe): Future[Int] = {
    db.run(RecipeTable.recipes
      .filter(_.id === id)
      .map(recipe => (recipe.title, recipe.making_time, recipe.serves, recipe.ingredients, recipe.cost, recipe.updatedAt))
      .update((updatedRecipe.title, updatedRecipe.making_time, updatedRecipe.serves, updatedRecipe.ingredients, updatedRecipe.cost, new Timestamp(System.currentTimeMillis())))
    ).map { rowsAffected =>
      if (rowsAffected == 0) throw new Exception(s"Recipe with ID $id not found")
      rowsAffected
    }
  }


  // Delete a recipe by ID
  // If recipe with given ID does not exist, throw an exception
  // Otherwise, return the number of rows affected
  def deleteRecipe(id: Int): Future[Int] = {
    db.run(RecipeTable.recipes.filter(_.id === id).delete)
    .map { rowsAffected =>
      if (rowsAffected == 0) throw new Exception(s"Recipe with ID $id not found")
      rowsAffected
    }
  }
}
