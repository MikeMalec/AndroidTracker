package com.example.tracker.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(@ApplicationContext val context: Context) {
    private val dataStore: DataStore<Preferences> = context.createDataStore("routes_preferences")

    private val stepsKey = preferencesKey<Int>("STEPS")

   suspend fun getSteps(): Flow<Int?> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[stepsKey]
        }
    }

    suspend fun saveSteps(steps: Int) {
        dataStore.edit { preferences ->
            preferences[stepsKey] = steps
        }
    }
}