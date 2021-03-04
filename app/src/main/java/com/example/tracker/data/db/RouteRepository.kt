package com.example.tracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.example.tracker.utils.Resource
import com.example.tracker.utils.safeCacheCall
import kotlinx.coroutines.Dispatchers.IO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRepository @Inject constructor(val routeDao: RouteDao) {
    suspend fun saveRoute(route: Route): Resource<Long?> {
        return safeCacheCall(IO) {
            routeDao.saveRoute(route)
        }
    }

    suspend fun deleteRoute(route: Route) = routeDao.deleteRoute(route)

    fun getRoutes() = routeDao.getRoutes()

    fun getTotalTime(): LiveData<Long> = routeDao.getTotalTime()

    fun getTotalDistance(): LiveData<Long> = routeDao.getTotalDistance()

    fun getTotalAvgSpeed(): LiveData<Long> = routeDao.getTotalAvgSpeed()

    fun getTotalSteps(): LiveData<Long> = routeDao.getTotalSteps()
}