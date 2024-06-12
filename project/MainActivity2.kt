package com.example.googlemap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.*

class MainActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mGoogleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            mapFragment.getMapAsync(this)
            getCurrentLocation()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
            mapFragment.getMapAsync(this)
            getCurrentLocation()
        } else {
            // Handle denied permission
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                    val currentLocationMarkerOptions = MarkerOptions()
                        .position(currentLatLng)
                    mGoogleMap?.addMarker(currentLocationMarkerOptions)

                    val restaurantCoordinates = listOf(
                        Pair(35.2419839, 128.6887205),
                        Pair(35.2420741, 128.6890553),
                        Pair(35.2382367, 128.6890614),
                        Pair(35.3118591, 128.6489146),
                        Pair(35.3118591, 128.6489146),
                        Pair(35.2421994, 128.6888197),
                        Pair(35.242029, 128.6888879),
                        Pair(35.2421861, 128.689473),
                        Pair(35.2423032, 128.6882382),
                        Pair(35.2411598, 128.6907497)
                    )

                    for ((index, coordinates) in restaurantCoordinates.withIndex()) {
                        val markerLatLng = LatLng(coordinates.first, coordinates.second)
                        val markerOptions = MarkerOptions()
                            .position(markerLatLng)
                            .title("Restaurant ${index + 1}")
                        mGoogleMap?.addMarker(markerOptions)
                    }
                }
            }
        }
    }
}
