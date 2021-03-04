package com.example.tracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters


@Database(
    entities = [Route::class],
    version = 1
)
@TypeConverters(Converter::class)
abstract class RouteDatabase : RoomDatabase() {
    abstract fun getRouteDao(): RouteDao
}