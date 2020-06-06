package com.skybase.mealtracker.ui.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.skybase.mealtracker.R
import com.skybase.mealtracker.databinding.ActivityMealLocationSelectorBinding

class MealLocationSelectorActivity : AppCompatActivity(), OnMapReadyCallback {

    private val REQUEST_LOCATION_PERMISSION = 1

    private lateinit var mMap: GoogleMap
    private lateinit var mBinding: ActivityMealLocationSelectorBinding

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mCurrentLocation = LatLng(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_meal_location_selector)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mBinding.btnConfirm.setOnClickListener { sendResultBack() }

        Toast.makeText(
            this,
            R.string.meal_location_activity_hint,
            Toast.LENGTH_LONG
        ).show()
    }

    //<editor-fold desc="override">
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMapLocation()
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="map callback">
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableMapLocation()
    }

    private fun enableMapLocation() {
        if (isPermissionGranted()) {
            mMap.isMyLocationEnabled = true
            getUsersCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun getUsersCurrentLocation() {
        mFusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    mCurrentLocation = LatLng(location.latitude, location.longitude)
                }
                addMarkerForCurrentLocation(location)
            }

    }

    private fun addMarkerForCurrentLocation(location: Location?) {
        location?.apply {
            mMap.clear()
            val userLocation = LatLng(latitude, longitude)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(userLocation)
                    .draggable(true)
                    .title("You are here")
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16f))

            mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDragEnd(p0: Marker?) {
                    mCurrentLocation = marker.position
                }

                override fun onMarkerDragStart(p0: Marker?) {

                }

                override fun onMarkerDrag(p0: Marker?) {

                }
            })
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    //</editor-fold>

    private fun sendResultBack() {
        val intent = Intent()
            .putExtra(
                resources.getString(R.string.meal_location_intent_lat),
                mCurrentLocation.latitude
            ).putExtra(
                resources.getString(R.string.meal_location_intent_lng),
                mCurrentLocation.longitude
            )
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
