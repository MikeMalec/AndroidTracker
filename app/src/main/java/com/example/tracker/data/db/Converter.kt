package com.example.tracker.data.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayOutputStream
import java.lang.Exception

class Converter {
    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @TypeConverter
    fun fromBitmap(bmp: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toListOfLatLng(string: String): List<LatLng> {
        val values = string.split("|").filter { it.length > 3 }
        return values.map { str ->
            val value = str.split(',')
            val lat = value[0]
            val lng = value[1]
            LatLng(lat.toDouble(), lng.toDouble())
        }
    }

    @TypeConverter
    fun fromListOfLatLng(positions: List<LatLng>): String {
        val positionsSize = positions.size
        var str = ""
        positions.forEachIndexed { index, latLng ->
            str += if (positionsSize == index) {
                "${latLng.latitude},${latLng.longitude}"
            } else {
                "${latLng.latitude},${latLng.longitude}|"
            }
        }
        return str
    }
}