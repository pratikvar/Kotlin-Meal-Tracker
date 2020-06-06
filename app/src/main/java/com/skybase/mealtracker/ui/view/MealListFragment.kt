package com.skybase.mealtracker.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.skybase.mealtracker.R
import com.skybase.mealtracker.databinding.FragmentMealListBinding
import com.skybase.mealtracker.global.MealApplication
import com.skybase.mealtracker.ui.Meal
import com.skybase.mealtracker.ui.view.adapter.MealListAdapter
import com.skybase.mealtracker.ui.viewmodel.MealListFragmentViewModel
import com.skybase.mealtracker.ui.viewmodel.MealViewModelFactory

class MealListFragment : Fragment(), MealListAdapter.MealClickListener {

    private lateinit var mBinding: FragmentMealListBinding
    private lateinit var mViewModel: MealListFragmentViewModel

    private val mAdapter: MealListAdapter by lazy { MealListAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMealListBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupViewModel()
        setupListeners()
        return mBinding.root
    }

    private fun setupRecyclerView() {
        mBinding.rvMeals.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvMeals.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.HORIZONTAL
            )
        )
        mBinding.rvMeals.adapter = mAdapter
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProviders.of(
            this,
            MealViewModelFactory(MealApplication.mealDatabase.mealDao())
        )
            .get(MealListFragmentViewModel::class.java)

        mViewModel.mealList.observe(viewLifecycleOwner, Observer { t -> updateMealList(t) })
    }

    private fun updateMealList(mealList: List<Meal>?) {
        if (mealList.isNullOrEmpty()) {
            mBinding.tvEmpty.visibility = View.VISIBLE
            mAdapter.updateDataSet(mutableListOf())
        } else {
            mBinding.tvEmpty.visibility = View.GONE
            mAdapter.updateDataSet(mealList)
        }
    }

    private fun setupListeners() {
        mBinding.mealFab.setOnClickListener {
            findNavController().navigate(R.id.action_mealListFragment_to_mealAddFragment)
        }
    }

    override fun onMealClicked(meal_id: Long) {
        findNavController().navigate(
            MealListFragmentDirections.actionMealListFragmentToMealDetailsFragment(
                mealId = meal_id
            )
        )
    }

}
