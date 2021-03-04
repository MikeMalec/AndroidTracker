package com.example.tracker.ui.fragments.stats

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.tracker.data.db.RouteRepository

class StatisticsViewModel @ViewModelInject constructor(
    private val routeRepository: RouteRepository
) : ViewModel() {
    val totalTime = routeRepository.getTotalTime()
    val totalDistance = routeRepository.getTotalDistance()
    val totalAvgSpeed = routeRepository.getTotalAvgSpeed()
    val totalSteps = routeRepository.getTotalSteps()
}