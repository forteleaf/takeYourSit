package com.forteleaf.takeyoursit

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * database instance 생성
 * dao에 기반하여 저장소 생성
 * 필요할 때만 만들어야 하므로 by lazy 을 사용
 */
class DevicesApplication : Application() {
    // 이 범위는 프로세스와 함께 삭제되므로 취소할 필요가 없습니다.
    val applicationScope = CoroutineScope(SupervisorJob())

    // 데이터베이스를 초기화합니다.
    val database by lazy { DeviceRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { DeviceRepository(database.deviceDao()) }
}
