package com.forteleaf.takeyoursit

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = arrayOf(Device::class), version = 1)
abstract class AppDatabase :RoomDatabase(){
    abstract fun deviceDao(): DeviceDao

    // singleton
    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }
        // singleton
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // populate the database in the background.
                        // if you want to start with more words, just add them.
                        // instance?.let { database ->
                        //     scope.launch(Dispatchers.IO) {
                        //         populateDatabase(database.wordDao())
                        //     }
                        // }
                    }
                })
                .build()

//        private fun buildDatabase(context: Context): AppDatabase {
//            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
//                .addCallback(
//                    object : RoomDatabase.Callback() {
//                        override fun onCreate(db: SupportSQLiteDatabase) {
//                            super.onCreate(db)
//                        }
//                    }
//                )
//
//        }
    }
}