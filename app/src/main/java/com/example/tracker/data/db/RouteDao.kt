package com.example.tracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRoute(route: Route): Long

    @Delete
    suspend fun deleteRoute(route: Route)

    @Query("SELECT * FROM route_table ORDER BY createdAt DESC")
    fun getRoutes(): LiveData<List<Route>>

    @Query("SELECT SUM(totalTime) FROM route_table")
    fun getTotalTime(): LiveData<Long>

    @Query("SELECT SUM(totalDistance) FROM route_table")
    fun getTotalDistance(): LiveData<Long>

    @Query("SELECT AVG(averageSpeed) FROM route_table")
    fun getTotalAvgSpeed(): LiveData<Long>

    @Query("SELECT SUM(totalSteps) FROM route_table")
    fun getTotalSteps(): LiveData<Long>
}