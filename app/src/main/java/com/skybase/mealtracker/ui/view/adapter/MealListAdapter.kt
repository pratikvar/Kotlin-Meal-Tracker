package com.skybase.mealtracker.ui.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skybase.mealtracker.R
import com.skybase.mealtracker.databinding.ListitemMealBinding
import com.skybase.mealtracker.ui.Meal
import java.io.File

class MealListAdapter(val clickListeners: MealClickListener) :
    RecyclerView.Adapter<MealListAdapter.MealHolder>() {

    var mealList = mutableListOf<Meal>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealHolder {
        return MealHolder(
            ListitemMealBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MealHolder, position: Int) {
        val meal = mealList[position]
        holder.binding.meal = meal
        Glide.with(holder.binding.ivMeal.context)
            .load(File(meal.meal_image))
            .asBitmap()
            .placeholder(R.drawable.ic_meal_placeholder)
            .into(holder.binding.ivMeal)
    }

    override fun getItemCount(): Int {
        return mealList.size
    }

    fun updateDataSet(updatedList: List<Meal>) {
        mealList = updatedList.toMutableList()
        notifyDataSetChanged()
    }

    inner class MealHolder(val binding: ListitemMealBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListeners.onMealClicked(mealList[adapterPosition].meal_id)
        }
    }

    class MealDiffUtil : DiffUtil.ItemCallback<Meal>() {
        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem.meal_id == newItem.meal_id
        }

        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem == newItem
        }
    }

    interface MealClickListener {
        fun onMealClicked(meal_id: Long)
    }
}
