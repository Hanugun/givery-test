package models

import java.sql.Timestamp

case class Recipe(
  id: Option[Int],
  title: String,
  making_time: String,
  serves: String,
  ingredients: String,
  cost: Int,
  createdAt: Option[Timestamp] = None,
  updatedAt: Option[Timestamp] = None
)
