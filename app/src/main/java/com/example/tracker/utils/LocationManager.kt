package com.example.tracker.utils

import android.annotation.SuppressLint
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(val fusedLocationProviderClient: FusedLocationProviderClient) {
    private val _position: MutableLiveData<LatLng> = MutableLiveData()
    val position: LiveData<LatLng> = _position

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            result?.locations?.let { locations ->
                locations.forEach {
                    _position.postValue(LatLng(it.latitude, it.longitude))
                }
            }
        }
    }

    private fun getLocationRequest(): LocationRequest {
        return LocationRequest().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = FASTEST_LOCATION_INTERVAL
            priority = PRIORITY_HIGH_ACCURACY
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            getLocationRequest(), locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}