package com.skybase.mealtracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skybase.mealtracker.database.entities.MealEntity
import com.skybase.mealtracker.ui.Meal

@Dao
interface MealEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeal(meal: MealEntity)

    @Query("SELECT * FROM Meal ORDER BY meal_id DESC")
    fun getAllMeals(): LiveData<List<Meal>>

    @Query("SELECT * FROM Meal WHERE meal_id=:mealId")
    fun getMealById(mealId: Long): LiveData<Meal>

    @Query("DELETE FROM Meal WHERE meal_id=:mealId")
    fun deleteMeal(mealId: Long)

    @Query("DELETE FROM Meal")
    fun deleteAllMeals()
}