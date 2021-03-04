package com.example.tracker.data.api

import com.example.tracker.utils.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiService @Inject constructor(val positionApi: PositionApi) {
    suspend fun sendPosition(positionRequest: PositionRequest) {
        safeApiCall {
            positionApi.sendPosition(positionRequest)
        }
    }
}