package com.skybase.mealtracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skybase.mealtracker.database.dao.MealEntityDao
import com.skybase.mealtracker.database.entities.EntityConverter
import com.skybase.mealtracker.database.entities.MealEntity

@Database(version = 1, entities = [MealEntity::class], exportSchema = true)
@TypeConverters(EntityConverter::class)
abstract class MealDatabase : RoomDatabase() {

    abstract fun mealDao(): MealEntityDao

    companion object {

        @Volatile
        private var INSTANCE: MealDatabase? = null

        internal fun getMealDatabase(context: Context): MealDatabase {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE =
                        Room.databaseBuilder(context, MealDatabase::class.java, "meal_database")
                            .build()
                }
                return INSTANCE as MealDatabase
            }
        }
    }

}