package com.forteleaf.takeyoursit

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DeviceDao {
    @Query("SELECT * FROM device")
    fun getAll(): List<Device>

    //insert
    @Insert
    fun insert(device: Device)

    @Delete
    fun delete(device: Device)

}