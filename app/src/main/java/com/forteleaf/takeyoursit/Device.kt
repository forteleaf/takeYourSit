package com.forteleaf.takeyoursit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_table")
data class Device(
    // bluetooth device data
    @PrimaryKey @ColumnInfo(name= "address") val address: String,
    val name: String,
    val rssi: Int,
    val distance: Double,
    val timestamp: Long
)


