package com.skybase.mealtracker.ui

import com.skybase.humanizer.DateHumanizer
import com.skybase.mealtracker.database.entities.MealLocation
import com.skybase.mealtracker.database.entities.MealType
import java.util.*

data class Meal(
    var meal_id: Long,
    var meal_name: String,
    var meal_info: String,
    var meal_type: MealType,
    var meal_timing: Long = Calendar.getInstance().timeInMillis,
    var meal_restaurant: String,
    var meal_location: MealLocation,
    var meal_image: String
) {
    fun getMealTimeOnly(): String {
        return DateHumanizer.humanize(
            meal_timing,
            DateHumanizer.TYPE_DATE_DISABLE,
            DateHumanizer.TYPE_TIME_HH_MM_A
        )
    }

    fun getMealDateTime(): String {
        return DateHumanizer.humanize(
            meal_timing,
            DateHumanizer.TYPE_DD_MMM_YYYY,
            DateHumanizer.TYPE_TIME_HH_MM_A
        )
    }
}