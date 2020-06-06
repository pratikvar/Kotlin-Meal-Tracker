package com.skybase.mealtracker.global

import android.app.Application
import com.skybase.mealtracker.database.MealDatabase

class MealApplication : Application() {

    companion object {
        lateinit var mealDatabase: MealDatabase
    }

    override fun onCreate() {
        super.onCreate()

        synchronized(this) {
            mealDatabase = MealDatabase.getMealDatabase(this)
        }
    }

}