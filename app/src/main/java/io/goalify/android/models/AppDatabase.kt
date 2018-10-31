package io.goalify.android.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Goal::class, GoalDate::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao
    abstract fun goalDateDao(): GoalDateDao

    companion object {
        private var instance: AppDatabase? = null

        fun createInstance(context: Context) {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "goalify.db"
                    )
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()

//                    instance = Room.inMemoryDatabaseBuilder(
//                        context,
//                        AppDatabase::class.java
//                    )
//                        .fallbackToDestructiveMigration()
//                        .allowMainThreadQueries()
//                        .build()
                }
            }
        }

        fun getInstance(): AppDatabase {
            return instance ?: throw Exception("Cannot get instance of database without calling createInstance first")
        }
    }
}
