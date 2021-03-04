package com.example.tracker.data.api

import retrofit2.http.Body
import retrofit2.http.POST

interface PositionApi {
    @POST("positions")
    suspend fun sendPosition(
        @Body position: PositionRequest
    ): GenericResponse
}