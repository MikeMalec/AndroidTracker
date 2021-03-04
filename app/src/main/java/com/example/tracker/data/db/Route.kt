package com.example.tracker.data.db

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "route_table")
@Parcelize
data class Route(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val totalTime: Long,
    val totalDistance: Int,
    val averageSpeed: Double,
    val totalSteps: Int,
    val img: Bitmap,
    val positions: List<LatLng>,
    val createdAt: Long,
    val createdAtString: String,
) : Parcelable