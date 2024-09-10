package repositories

import models._
import slick.jdbc.MySQLProfile.api._
import java.sql.Timestamp

object RecipeTable {
  val recipes = TableQuery[RecipeTable]

  class RecipeTable(tag: Tag) extends Table[Recipe](tag, "recipes") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def making_time = column[String]("making_time")
    def serves = column[String]("serves")
    def ingredients = column[String]("ingredients")
    def cost = column[Int]("cost")
    def createdAt = column[Timestamp]("created_at")
    def updatedAt = column[Timestamp]("updated_at")

    def * = (id.?, title, making_time, serves, ingredients, cost, createdAt.?, updatedAt.?) <> (Recipe.tupled, Recipe.unapply)
  }
}
