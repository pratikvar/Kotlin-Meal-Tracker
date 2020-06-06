package com.skybase.mealtracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skybase.mealtracker.R
import com.skybase.mealtracker.database.dao.MealEntityDao
import com.skybase.mealtracker.database.entities.MealLocation
import com.skybase.mealtracker.database.entities.MealType
import com.skybase.mealtracker.database.entities.convertToMealEntity
import com.skybase.mealtracker.ui.Meal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MealAddFragmentViewModel(val database: MealEntityDao) : ViewModel() {

    private val _isMealSaved = MutableLiveData<Boolean>()
    val isMealSaved: LiveData<Boolean>
        get() = _isMealSaved

    private val _toastMessage = MutableLiveData<Int?>()
    val toastMessage: LiveData<Int?>
        get() = _toastMessage

    var currentMeal = MutableLiveData<Meal>()

    init {
        currentMeal.value = Meal(
            meal_id = 0, meal_name = "", meal_info = "", meal_type = getCurrentMealType(),
            meal_restaurant = "", meal_location = MealLocation(), meal_image = ""
        )
    }

    private fun getCurrentMealType(): MealType {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 4..11 -> MealType.BREAKFAST
            in 12..15 -> MealType.LUNCH
            in 16..18 -> MealType.LIGHT_SNACKS
            else -> MealType.DINNER
        }
    }

    fun setMealLocation(mealLocation: MealLocation) {
        currentMeal.value = currentMeal.value?.also {
            it.meal_location = mealLocation
        }
    }

    fun setMealTiming(mealTiming: Long) {
        currentMeal.value = currentMeal.value?.also {
            it.meal_timing = mealTiming
        }
    }

    fun setMealType(mealType: MealType) {
        currentMeal.value = currentMeal.value?.also {
            it.meal_type = mealType
        }
    }

    fun setMealImage(imageUrl: String) {
        currentMeal.value = currentMeal.value?.also {
            it.meal_image = imageUrl
        }
    }

    fun saveMealData() {
        if (isMealValid())
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    database.insertMeal(meal = currentMeal.value!!.convertToMealEntity())
                    _isMealSaved.postValue(true)
                }
            }
    }

    private fun isMealValid(): Boolean {
        if (currentMeal.value!!.meal_image.isNullOrEmpty()) {
            _toastMessage.value = R.string.meal_add_image_validation
            return false
        }
        if (currentMeal.value!!.meal_name.isNullOrEmpty()) {
            _toastMessage.value = R.string.meal_add_name_validation
            return false
        }
        if (currentMeal.value!!.meal_info.isNullOrEmpty()) {
            _toastMessage.value = R.string.meal_add_info_validation
            return false
        }
        if (currentMeal.value!!.meal_restaurant.isNullOrEmpty()) {
            _toastMessage.value = R.string.meal_add_restaurant_validation
            return false
        }
        if (currentMeal.value!!.meal_location.lat == 0.0) {
            _toastMessage.value = R.string.meal_add_location_validation
            return false
        }
        return true
    }

    fun toastDisplayCompleted() {
        _toastMessage.value = null
    }

    fun onMealSavedCompleted() {
        _isMealSaved.postValue(null)
    }

}