package com.skybase.mealtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skybase.mealtracker.database.dao.MealEntityDao

class MealViewModelFactory(
    private val dataSource: MealEntityDao
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealListFragmentViewModel::class.java)) {
            return MealListFragmentViewModel(dataSource) as T
        }
        if (modelClass.isAssignableFrom(MealAddFragmentViewModel::class.java)) {
            return MealAddFragmentViewModel(dataSource) as T
        }
        if (modelClass.isAssignableFrom(MealDetailsFragmentViewModel::class.java)) {
            return MealDetailsFragmentViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}