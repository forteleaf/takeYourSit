package com.forteleaf.takeyoursit

import android.app.Application

/**
 * database instance 생성
 * dao에 기반하여 저장소 생성
 * 필요할 때만 만들어야 하므로 by lazy 을 사용
 */
class DevicesApplication : Application() {
    val database by lazy { DeviceRoomDatabase.getDatabase(this) }
    val repository by lazy { DeviceRepository(database.deviceDao()) }
}
