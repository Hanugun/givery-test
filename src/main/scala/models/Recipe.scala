package models

import java.sql.Timestamp

// Case class representing a recipe
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
