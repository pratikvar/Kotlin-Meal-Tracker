package com.skybase.mealtracker.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Meal")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val meal_id: Long,
    val meal_name: String,
    val meal_info: String,
    val meal_type: MealType,
    val meal_timing: Long,
    val meal_restaurant: String,
    val meal_location: MealLocation,
    val meal_image: String
)
