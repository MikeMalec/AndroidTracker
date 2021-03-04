package com.example.tracker.ui.fragments.routes

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracker.data.db.Route
import com.example.tracker.data.db.RouteRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class RoutesViewModel @ViewModelInject constructor(
    private val routeRepository: RouteRepository
) : ViewModel() {
    val routes = routeRepository.getRoutes()

    var didShimmer = false

    private val _routeDeleted = MutableLiveData<Boolean>()
    val routeDeleted: MutableLiveData<Boolean> = _routeDeleted

    fun deleteRoute(route: Route) {
        viewModelScope.launch(IO) {
            routeRepository.deleteRoute(route)
            routeDeleted.postValue(true)
        }
    }
}