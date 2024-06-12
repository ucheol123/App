package com.example.project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mGoogleMap: GoogleMap? = null
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 위치 권한이 없는 경우 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
        } else {
            // 위치 권한이 있는 경우 지도 및 현재 위치 가져오기
            mapFragment.getMapAsync(this)
            getCurrentLocation()
        }

        val buttonSearch: Button = findViewById(R.id.buttonSearch)
        buttonSearch.setOnClickListener {
            // 검색 기능은 작동하지 않도록 변경
            // 필요 시 추가 로직 구현 필요
        }

        val buttonMyPage: Button = findViewById(R.id.buttonMyPage)
        buttonMyPage.setOnClickListener {
            // 마이페이지로 이동하는 기능은 작동하지 않도록 변경
            // 필요 시 추가 로직 구현 필요
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mapFragment.getMapAsync(this)
                    getCurrentLocation()
                } else {
                    // 위치 권한이 거부된 경우에 대한 처리
                }
                return
            }
            else -> {
                // 다른 요청에 대한 처리
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        // 현재 위치 가져오는 로직은 작동하지 않도록 변경
        // 필요 시 추가 로직 구현 필요
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1
    }
}
