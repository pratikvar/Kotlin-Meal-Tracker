package com.skybase.mealtracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.skybase.mealtracker.database.dao.MealEntityDao
import com.skybase.mealtracker.ui.Meal

class MealListFragmentViewModel(database: MealEntityDao) : ViewModel() {

    private var _mealList: LiveData<List<Meal>> = database.getAllMeals()

    val mealList: LiveData<List<Meal>>
        get() = _mealList

}