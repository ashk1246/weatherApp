package com.kotlin.weather.ui.weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kotlin.mvvm.utils.extensions.toast
import com.kotlin.weather.base.BaseActivity
import com.kotlin.weather.databinding.ActivityNewWeatherBinding
import com.kotlin.weather.repository.api.network.Status
import com.kotlin.weather.ui.search.CountriesActivity
import com.kotlin.weather.utils.LogUtil
import com.kotlin.weather.utils.extensions.load
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Ashok
 */

@AndroidEntryPoint
class WeatherActivity : BaseActivity<ActivityNewWeatherBinding>() {

    private lateinit var adapter: WeatherAdapter
    private val weatherArticleViewModel: WeatherViewModel by viewModels()
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null

    /**
     * Create Binding
     */
    override fun createBinding(): ActivityNewWeatherBinding {
        return ActivityNewWeatherBinding.inflate(layoutInflater)
    }

    /**
     * On Create Of Activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.newsList.setEmptyView(binding.progressView.progressView)
        binding.newsList.setProgressView(binding.emptyView.emptyView)

        adapter = WeatherAdapter()
        adapter.onNewsClicked = {
            // TODO Your news item click invoked here
        }
        binding.search.setOnClickListener {
            val intent = Intent(this, CountriesActivity::class.java)
            activityResultLaunch.launch(intent)
        }

        binding.newsList.adapter = adapter
        binding.newsList.layoutManager = LinearLayoutManager(this)
    }
    public override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions()
            }
        } else {
            getLastLocation()
        }
    }
    private fun getLastLocation() {
        fusedLocationClient?.lastLocation!!.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = task.result
                getLatestWeather(lastLocation!!.altitude.toString(), lastLocation!!.longitude.toString())
            } else {
                Log.w(TAG, "getLastLocation:exception", task.exception)
            }
        }
    }
    private fun showSnackbar(
        mainTextStringId: String,
        actionStringId: String,
        listener: View.OnClickListener
    ) {
        Toast.makeText(this, mainTextStringId, Toast.LENGTH_LONG).show()
    }
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }
    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }
    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar(
                "Location permission is needed for core functionality",
                "Okay",
                View.OnClickListener {
                    startLocationPermissionRequest()
                }
            )
        } else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.")
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission granted.
                    getLastLocation()
                }
                else -> {
                    showSnackbar(
                        "Permission was denied",
                        "Settings",
                        View.OnClickListener {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                Build.DISPLAY,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
    companion object {
        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    /**
     * Get country news using Network & DB Bound Resource
     * Observing for data change from DB and Network Both
     */
    private fun getLatestWeather(latKey: String, lanKey: String) {
        weatherArticleViewModel.getWeatherCache(latKey, lanKey).observe(this) {
            when {
                it.status.isLoading() -> {
                    binding.newsList.showProgressView()
                }
                it.status.isSuccessful() -> {
                    it?.load(binding.newsList) { weather ->
                        adapter.replaceItems(weather ?: emptyList())
                    }
                }
                it.status.isError() -> {
                    toast(it.errorMessage ?: "Something went wrong.")
                    finish()
                }
            }
        }
    }

    private fun getCoordinates(place: String) {
        weatherArticleViewModel.searchFromAPI(place).observe(this) {
            when {
                it.status.isLoading() -> {
//                    binding.newsList.showProgressView()
                }
                it.status.isSuccessful() -> {
                    it?.load(binding.newsList) { weather ->
                        binding.newsList.showState(Status.SUCCESS)
                        getLatestWeather(weather?.get(0)!!.lat.toString(), weather?.get(0)!!.lon.toString())
                        println("coordinates_ashk::LAT::${weather?.get(0)!!.lat} :: ${weather?.get(0)!!.lon}")
                    }
                }
                it.status.isError() -> {
                    toast(it.errorMessage ?: "Something went wrong.")
                    finish()
                }
            }
        }
    }
    var activityResultLaunch = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == LogUtil.RESULT_CODE) {
            result.data?.getStringExtra(CountriesActivity.RESULT_DATA)?.let { getCoordinates(it) }
        }
    }
}
