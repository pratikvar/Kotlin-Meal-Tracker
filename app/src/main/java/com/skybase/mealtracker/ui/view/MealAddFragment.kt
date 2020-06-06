package com.skybase.mealtracker.ui.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.skybase.mealtracker.R
import com.skybase.mealtracker.database.entities.MealLocation
import com.skybase.mealtracker.database.entities.MealType
import com.skybase.mealtracker.databinding.FragmentMealAddBinding
import com.skybase.mealtracker.global.MealApplication
import com.skybase.mealtracker.ui.viewmodel.MealAddFragmentViewModel
import com.skybase.mealtracker.ui.viewmodel.MealViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MealAddFragment : Fragment() {

    private val REQUEST_LOCATION_FROM_MAP = 253
    private val REQUEST_IMAGE_CAPTURE = 254

    lateinit var mBinding: FragmentMealAddBinding
    lateinit var mViewModel: MealAddFragmentViewModel

    lateinit var currentPhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMealAddBinding.inflate(inflater, container, false)
        mBinding.lifecycleOwner = viewLifecycleOwner

        registerViewModel()
        setupListeners()
        return mBinding.root
    }

    //<editor-fold desc="override">
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION_FROM_MAP -> handleUserLocationResult(resultCode, data)
            REQUEST_IMAGE_CAPTURE -> handleImageCaptureResult(resultCode, data)
        }
    }

    private fun handleUserLocationResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            mViewModel.setMealLocation(
                MealLocation(
                    data.getDoubleExtra(
                        resources.getString(R.string.meal_location_intent_lat),
                        0.0
                    ),
                    data.getDoubleExtra(resources.getString(R.string.meal_location_intent_lng), 0.0)
                )
            )

        } else {
            Toast.makeText(
                requireContext(),
                R.string.meal_location_retrieve_failed,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleImageCaptureResult(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            saveCompressedImage(currentPhotoPath)
            mViewModel.setMealImage(currentPhotoPath)
        } else {
            Toast.makeText(
                requireContext(),
                R.string.meal_image_capture_failed,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveCompressedImage(currentPhotoPath: String) {
        val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
        try {
            val out = FileOutputStream(currentPhotoPath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)
            out.flush();
            out.close();
        } catch (e: Exception) {
            Log.e("Failed", "Resize Failed")
        }
    }

    private fun showMealTimeSelector() {
        val calender = Calendar.getInstance()
        val timeSelected =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calender.set(Calendar.MINUTE, minute)
                mViewModel.setMealTiming(calender.timeInMillis)
            }

        val timePicker = TimePickerDialog(
            requireContext(),
            timeSelected,
            calender.get(Calendar.HOUR_OF_DAY),
            calender.get(Calendar.MINUTE),
            false
        )
        if (!timePicker.isShowing)
            timePicker.show()
    }
    //</editor-fold>

    //<editor-fold desc="init">
    private fun registerViewModel() {
        mViewModel = ViewModelProviders.of(
            this,
            MealViewModelFactory(MealApplication.mealDatabase.mealDao())
        )
            .get(MealAddFragmentViewModel::class.java)

        mBinding.meal = mViewModel

        mViewModel.isMealSaved.observe(viewLifecycleOwner, Observer {
            it?.run {
                if (it) {
                    mViewModel.onMealSavedCompleted()
                    findNavController().navigateUp()
                }
            }
        })

        mViewModel.toastMessage.observe(viewLifecycleOwner, Observer {
            it?.let { resourceId ->
                Toast.makeText(requireContext(), resourceId as Int, Toast.LENGTH_SHORT).show()
                mViewModel.toastDisplayCompleted()
            }
        })
    }

    private fun setupListeners() {
        mBinding.ivMeal.setOnClickListener { initializeCameraIntent() }

        mBinding.layoutMealTime.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showMealTimeSelector()
            }
        }

        mBinding.layoutMealLocation.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                startActivityForResult(
                    Intent(
                        requireActivity(),
                        MealLocationSelectorActivity::class.java
                    ), REQUEST_LOCATION_FROM_MAP
                )
            }
        }

        setupChips()
    }

    private fun setupChips() {
        when (mViewModel.currentMeal.value?.meal_type) {
            MealType.BREAKFAST -> mBinding.chip1.performClick()
            MealType.LUNCH -> mBinding.chip2.performClick()
            MealType.LIGHT_SNACKS -> mBinding.chip3.performClick()
            MealType.DINNER -> mBinding.chip4.performClick()
        }

        val clickListener = { chip: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                mViewModel.setMealType(MealType.valueOf(chip.text.toString()))
            }
        }

        mBinding.chip1.setOnCheckedChangeListener(clickListener)
        mBinding.chip2.setOnCheckedChangeListener(clickListener)
        mBinding.chip3.setOnCheckedChangeListener(clickListener)
        mBinding.chip4.setOnCheckedChangeListener(clickListener)
    }
    //</editor-fold>

    //<editor-fold desc="camera">
    private fun initializeCameraIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    getMealImageFilePath()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.skybase.mealtracker.fileprovider",
                        it
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun getMealImageFilePath(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image: File = File.createTempFile(
            "${resources.getString(R.string.app_name).replace(" ", "_")}_$timeStamp",
            ".jpg",
            storageDir
        )
        currentPhotoPath = image.absolutePath
        return image
    }
    //</editor-fold>

}
