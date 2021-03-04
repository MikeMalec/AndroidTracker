package com.example.tracker.utils

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T?
): Resource<T?> {
    return withContext(IO) {
        try {
            withTimeout(6000) {
                Resource.Success(apiCall())
            }
        } catch (throwable: Throwable) {
//            Log.d("XXX", "$throwable")
            throwable.printStackTrace()
            Resource.Error()
        }
    }
}

suspend fun <T> safeCacheCall(
    dispatcher: CoroutineDispatcher,
    cacheCall: suspend () -> T
): Resource<T?> {
    return withContext(dispatcher) {
        try {
            withTimeout(3000) {
                Resource.Success(cacheCall.invoke())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    Resource.Error("ERROR TIMEOUT")
                }
                else -> {
                    Resource.Error("ERROR UNKNOWN")
                }
            }
        }
    }
}

sealed class Resource<out T> {
    data class Loading<out T>(val data: T? = null) : Resource<T>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val error: String? = null) : Resource<Nothing>()
}