package com.skybase.mealtracker.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.skybase.mealtracker.database.dao.MealEntityDao

class MealDetailsFragmentViewModel(val database: MealEntityDao) : ViewModel() {

    private val mealId = MutableLiveData<Long>()

    val currentMeal = Transformations.switchMap(mealId) { input ->
        database.getMealById(input)
    }

    fun setMealId(id: Long) {
        mealId.value = id
    }

}