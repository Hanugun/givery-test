package models

import java.sql.Timestamp
case class RecipeData(title: String, makingTime: String, serves: String, ingredients: String, cost: Int)

case class Recipe(
  id: Option[Int],
  title: String,
  makingTime: String,
  serves: String,
  ingredients: String,
  cost: Int,
  createdAt: Option[Timestamp] = None,
  updatedAt: Option[Timestamp] = None
)
