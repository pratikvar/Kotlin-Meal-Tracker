package com.skybase.mealtracker.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.skybase.mealtracker.databinding.FragmentMealDetailsBinding
import com.skybase.mealtracker.global.MealApplication
import com.skybase.mealtracker.ui.viewmodel.MealDetailsFragmentViewModel
import com.skybase.mealtracker.ui.viewmodel.MealViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class MealDetailsFragment : Fragment() {

    private lateinit var mBinding: FragmentMealDetailsBinding
    private lateinit var mViewModel: MealDetailsFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMealDetailsBinding.inflate(inflater, container, false)
        mBinding.lifecycleOwner = viewLifecycleOwner

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()

        mBinding.ivLocation.setOnClickListener { openGoogleMap() }
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProviders.of(
            this,
            MealViewModelFactory(MealApplication.mealDatabase.mealDao())
        )
            .get(MealDetailsFragmentViewModel::class.java)
        mViewModel.setMealId(MealDetailsFragmentArgs.fromBundle(requireArguments()).mealId)
        mViewModel.currentMeal.observe(viewLifecycleOwner, Observer {
            mBinding.meal = it

        })
    }

    private fun openGoogleMap() {
        val gmmIntentUri = Uri.parse(
            ("http://maps.google.com/maps?q=loc:"
                    + mViewModel.currentMeal.value!!.meal_location)
        )
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        startActivity(Intent.createChooser(mapIntent, "Show Location in"))
    }

}
