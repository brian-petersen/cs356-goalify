package io.goalify.android.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Goal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao

    companion object {
        private var instance: AppDatabase? = null

        fun createInstance(context: Context) {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    // TODO uncomment this to save across app life cycles
//                    instance = Room.databaseBuilder(
//                        context,
//                        AppDatabase::class.java,
//                        "goalify.db"
//                    )
//                        .allowMainThreadQueries()
//                        .build()

                    instance = Room.inMemoryDatabaseBuilder(
                        context,
                        AppDatabase::class.java
                    )
                        .allowMainThreadQueries()
                        .build()
                }
            }
        }

        fun getInstance(): AppDatabase? {
            return instance
        }
    }
}
