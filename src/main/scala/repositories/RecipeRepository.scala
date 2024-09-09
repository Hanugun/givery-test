package repositories

import models._
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.{Future, ExecutionContext}

class RecipeRepository(db: Database)(implicit ec: ExecutionContext) {
  // Fetch all recipes
  def getAllRecipes: Future[Seq[Recipe]] = {
    db.run(RecipeTable.recipes.result)
  }

  // Fetch a recipe by ID
  def getRecipeById(id: Int): Future[Option[Recipe]] = {
    db.run(RecipeTable.recipes.filter(_.id === id).result.headOption)
  }

  // Insert a new recipe
  def insertRecipe(recipe: Recipe): Future[Int] = {
    db.run(RecipeTable.recipes returning RecipeTable.recipes.map(_.id) += recipe)
  }

  // Update an existing recipe
  def updateRecipe(id: Int, updatedRecipe: Recipe): Future[Int] = {
    db.run(RecipeTable.recipes.filter(_.id === id).update(updatedRecipe))
  }

  // Delete a recipe by ID
  def deleteRecipe(id: Int): Future[Int] = {
    db.run(RecipeTable.recipes.filter(_.id === id).delete)
  }
}
