package com.skybase.mealtracker.database.entities

import androidx.room.TypeConverter
import com.skybase.mealtracker.ui.Meal

enum class MealType(i: Int) {
    BREAKFAST(1), LUNCH(2), DINNER(3), LIGHT_SNACKS(4)
}

data class MealLocation(var lat: Double, var lng: Double) {
    constructor() : this(0.0, 0.0)

    override fun toString(): String {
        return "$lat,$lng"
    }
}

class EntityConverter {

    @TypeConverter
    fun mealTypeToString(mealType: MealType): String {
        return mealType.name
    }

    @TypeConverter
    fun stringToMealType(name: String): MealType {
        return MealType.valueOf(name)
    }

    @TypeConverter
    fun mealLocationToString(mealLocation: MealLocation): String {
        return mealLocation.toString()
    }

    @TypeConverter
    fun stringToMealLocation(location: String): MealLocation {
        val locationSplit = location.split(",")
        return MealLocation(locationSplit[0].toDouble(), locationSplit[1].toDouble())
    }
}

fun Meal.convertToMealEntity(): MealEntity {
    return MealEntity(
        meal_id,
        meal_name,
        meal_info,
        meal_type,
        meal_timing,
        meal_restaurant,
        meal_location,
        meal_image
    )
}