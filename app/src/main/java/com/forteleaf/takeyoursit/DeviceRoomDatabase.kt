package com.forteleaf.takeyoursit

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 클래스를 Room 데이터베이스가 되도록 @Database로 주석 처리하고 주석 매개변수를 사용하여 데이터베이스에 속한 항목을 선언하고 버전 번호를 설정합니다. 각 항목은 데이터베이스에 만들어질 테이블에 상응합니다.
 * 데이터베이스 이전은 이 Codelab의 범위를 벗어나므로 exportSchema는 빌드 경고를 피하기 위해 false로 설정
 * 데이터베이스는 각 @Dao의 추상 'getter' 메서드를 통해 DAO를 노출합니다.
 * 데이터베이스는 앱에서 단일 인스턴스만 유지해야 하므로 싱글톤 패턴을 사용하여 구현합니다.
 * 데이터베이스를 구현하는 클래스에는 추상 'getter' 메서드가 있어야 합니다. 각 DAO는 데이터베이스에 액세스할 수 있도록 데이터베이스 인스턴스를 전달받습니다.
 * getDatabase는 싱글톤을 반환합니다.
 * 처음 액세스할 때 데이터베이스를 만들어 Room의 데이터베이스 빌더를 사용하여 WordRoomDatabase 클래스의 애플리케이션 컨텍스트에서 RoomDatabase 객체를 만들고 이름을 "device_database"로 지정합니다.
 */
@Database(entities = arrayOf(Device::class), version = 1, exportSchema = false)
public abstract class DeviceRoomDatabase : RoomDatabase() {
    // abstract fun deviceDao(): DeviceDao

    abstract fun deviceDao(): DeviceDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        // Singleton은 동시에 여러 데이터베이스 인스턴스가 열리는 것을 방지합니다.
        @Volatile
        private var INSTANCE: DeviceRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): DeviceRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            // INSTANCE가 null이 아니면 반환하고,
            // null이면 데이터베이스를 만듭니다.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DeviceRoomDatabase::class.java,
                    "device_database"
                )
                    .addCallback(DeviceDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class DeviceDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.deviceDao())
                    }
                }
            }

            suspend fun populateDatabase(deviceDao: DeviceDao) {
                // Start the app with a clean database every time.
                deviceDao.deleteAll()

                var device = Device("00:00:00:00:00:01", "Device1", -16, 1)
                deviceDao.insert(device)
                device = Device("00:00:00:00:00:02", "Device2", -16, 0)
                deviceDao.insert(device)
                // Not needed if you only populate on creation.
            }

        }
    }

}