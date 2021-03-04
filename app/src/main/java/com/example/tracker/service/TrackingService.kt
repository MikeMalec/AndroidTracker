package com.example.tracker.service

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.tracker.data.api.ApiService
import com.example.tracker.data.api.PositionRequest
import com.example.tracker.data.datastore.DataStoreRepository
import com.example.tracker.utils.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : LifecycleService(), SensorEventListener {

    @Inject
    lateinit var channelManager: ChannelManager

    @Inject
    lateinit var trackingNotificationManager: TrackingNotificationManager

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var sensorManager: SensorManager

    @Inject
    lateinit var stepSensor: Sensor

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    private var timeStarted = 0L

    private var initialSteps = 0

    companion object {
        val isTracking = MutableLiveData(false)
        val position = MutableLiveData<LatLng>()
        val positions = MutableLiveData<List<LatLng>>()
        val meters = MutableLiveData(0)
        val time = MutableLiveData(0L)
        val avgSpeedMetersPerMinute = MutableLiveData(0.0)
        val steps = MutableLiveData(0)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                START_SERVICE -> startForegroundService()
                STOP_SERVICE -> stopService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        lifecycleScope.launch {
            dataStoreRepository.getSteps().collect {
                setInitialStepsAndRegisterSensor(it)
            }
        }
        isTracking.postValue(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelManager.createNotificationChannel()
        }
        startForeground(TRACKING_NOTIFICATION_ID, trackingNotificationManager.getNotification())
        locationManager.requestLocationUpdates()
        observePosition()
        startTiming()
    }

    private var setInitialSteps = false
    private fun setInitialStepsAndRegisterSensor(steps: Int?) {
        if (!setInitialSteps) {
            setInitialSteps = true
            steps?.also { initialSteps = steps }
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun startTiming() {
        timeStarted = System.currentTimeMillis()
        lifecycleScope.launch(IO) {
            while (true) {
                val millis = System.currentTimeMillis() - timeStarted
                val seconds = millis / 1000
                time.postValue(millis)
                if (meters.value != null && seconds > 0) {
                    val avg = meters.value!!.toDouble() / seconds.toDouble()
                    avgSpeedMetersPerMinute.postValue(avg)
                }
                delay(50L)
            }
        }
    }

    private fun stopService() {
        time.postValue(0L)
        steps.postValue(0)
        isTracking.postValue(false)
        positions.postValue(listOf())
        meters.postValue(0)
        avgSpeedMetersPerMinute.postValue(0.0)
        stopForeground(true)
        stopSelf()
    }

    private fun observePosition() {
        lifecycleScope.launch {
            locationManager.position.observe(this@TrackingService, Observer {
                position.postValue(it)
                calculateMeters(it)
                addPositionForPolylines(it)
//                sendPosition(it)
            })
        }
    }

    private fun addPositionForPolylines(latLng: LatLng) {
        val fetchedPositions = positions.value
        if (fetchedPositions != null) {
            positions.postValue(listOf(*fetchedPositions.toTypedArray(), latLng))
        } else {
            positions.postValue(listOf(latLng))
        }
    }

    private fun calculateMeters(position: LatLng) {
        if (positions.value != null && positions.value!!.isNotEmpty()) {
            val positions = positions.value!!
            val last = positions.last()
            val result = FloatArray(1)
            Location.distanceBetween(
                last.latitude,
                last.longitude,
                position.latitude,
                position.longitude,
                result
            )
            val currentMeters = meters.value!!
            meters.postValue((result[0] + currentMeters).toInt())
        }
    }

    private fun sendPosition(position: LatLng) {
        lifecycleScope.launch(IO) {
            apiService.sendPosition(PositionRequest(position.latitude, position.longitude))
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.also {
            val totalSteps = it.values[0].toInt()
            saveSteps(totalSteps)
            steps.postValue(totalSteps - initialSteps)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    private fun saveSteps(newSteps: Int) {
        lifecycleScope.launch {
            dataStoreRepository.saveSteps(newSteps)
        }
    }
}