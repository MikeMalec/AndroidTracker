package com.example.tracker.di

import android.app.NotificationManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.room.Room
import com.example.tracker.data.api.PositionApi
import com.example.tracker.data.db.RouteDao
import com.example.tracker.data.db.RouteDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit.Builder {
        return Retrofit.Builder().baseUrl("https://.xyz/")
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun providePositionApi(retrofit: Retrofit.Builder): PositionApi {
        return retrofit.build().create(PositionApi::class.java)
    }

    @Singleton
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return FusedLocationProviderClient(context)
    }

    @Singleton
    @Provides
    fun provideSensorManager(@ApplicationContext context: Context): SensorManager {
        return context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    @Singleton
    @Provides
    fun provideStepSensor(sensorManager: SensorManager): Sensor {
        return sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    @Singleton
    @Provides
    fun provideRouteDatabase(
        @ApplicationContext context: Context
    ): RouteDatabase {
        return Room.databaseBuilder(context, RouteDatabase::class.java, "ROUTE_DB").build()
    }

    @Singleton
    @Provides
    fun provideRouteDao(database: RouteDatabase): RouteDao {
        return database.getRouteDao()
    }
}