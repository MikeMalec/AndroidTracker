package com.example.tracker.ui.fragments.map

import android.graphics.Bitmap
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracker.data.db.Route
import com.example.tracker.data.db.RouteRepository
import com.example.tracker.utils.DateManager
import com.example.tracker.utils.Event
import com.example.tracker.utils.Resource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MapViewModel @ViewModelInject constructor(private val routeRepository: RouteRepository) :
    ViewModel() {

    private val _saveResponse = MutableLiveData<Event<String>>()
    val saveResponse: MutableLiveData<Event<String>> = _saveResponse

    fun saveRoute(
        totalTime: Long,
        totalDistance: Int,
        averageSpeed: Double,
        totalSteps: Int,
        img: Bitmap,
        positions: List<LatLng>
    ) {
        viewModelScope.launch(IO) {
            val time = System.currentTimeMillis()
            val route = Route(
                totalTime = totalTime,
                totalDistance = totalDistance,
                averageSpeed = averageSpeed,
                totalSteps = totalSteps,
                img = img,
                positions = positions,
                createdAt = time,
                createdAtString = DateManager.getDateWithDayNameAndHours(time)
            )
            val response = routeRepository.saveRoute(route)
            when (response) {
                is Resource.Success -> _saveResponse.postValue(Event("Successfully saved last tracking!"))
                is Resource.Error -> _saveResponse.postValue(Event("Could not save last tracking"))
            }
        }
    }
}