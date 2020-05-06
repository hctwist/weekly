package com.twisthenry8gmail.weeklyphoenix

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Goal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "goals_db"

        @Volatile
        var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {

            INSTANCE ?: buildDatabase(context)
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
    }

    abstract fun goalsDao(): Goal.Dao
}