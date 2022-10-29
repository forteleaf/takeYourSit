package com.forteleaf.takeyoursit

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO(데이터 액세스 객체)에서 SQL 쿼리를 지정하여 메서드 호출과 연결합니다. 컴파일러는 SQL을 확인하고 @Insert와 같은 일반 쿼리의 편의 주석으로 쿼리를 생성합니다. Room은 DAO를 사용하여 코드를 위한 깔끔한 API를 만듭니다.
 * DAO는 인터페이스 또는 추상 클래스여야 합니다.
 * 기본적으로 모든 쿼리는 별도의 스레드에서 실행되어야 합니다.
 * Room에서는 Kotlin 코루틴을 지원합니다. 따라서 쿼리를 suspend 수정자로 주석 처리하고 코루틴이나 다른 정지 함수에서 호출할 수 있습니다.
 */
@Dao
interface DeviceDao {
    @Query("SELECT * FROM device_table")
    fun getAll(): List<Device>

    /**
     * suspend 에 대한 설명
     * coroutine에서는 일시 중단 후 재개가 가능한 함수를 suspend 함수라고 합니다.
     * https://kotlinworld.com/144
     */
    @Insert
    suspend fun insert(device: Device)

    @Delete
    suspend fun delete(device: Device)

    /**
     * 데이터베이스 변경사항 관찰
     * https://developer.android.com/training/data-storage/room/observing-data?hl=ko
     * Flow는 값의 비동기 시퀀스입니다.
     * Flow는 네트워크 요청이나 데이터베이스 호출, 기타 비동기 코드 등의 비동기 작업에서 값을 생성할 수 있는 값을 한 번에 모두가 아니라 한 번에 하나씩 생성합니다. API 전체에서 코루틴을 지원하므로 코루틴을 사용하여 흐름도 변환할 수 있습니다.
     */
    @Query("SELECT * FROM device_table ORDER BY name ASC")
    fun getAlphabetizedDevices(): Flow<List<Device>>

    @Query("DELETE FROM device_table")
    suspend fun deleteAll()
}