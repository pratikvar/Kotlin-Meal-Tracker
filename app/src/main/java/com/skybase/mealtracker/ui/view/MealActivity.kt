package com.skybase.mealtracker.ui.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.skybase.mealtracker.R

class MealActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)

        if (resources.getString(R.string.google_maps_key) == "ADD_API_KEY") {
            Toast.makeText(
                this,
                "Add Map API key to make this app works properly",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
