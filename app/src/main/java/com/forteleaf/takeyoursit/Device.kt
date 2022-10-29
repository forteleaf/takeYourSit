package com.forteleaf.takeyoursit

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Device(
    // bluetooth device data
    @PrimaryKey val address: String,
    val name: String,
    val rssi: Int,
    val distance: Double,
    val timestamp: Long
)


