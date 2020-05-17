package com.twisthenry8gmail.weeklyphoenix.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Goal::class], version = 1)
abstract class RoomModel : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "goals_db"

        @Volatile
        private var INSTANCE: RoomModel? = null

        fun getInstance(context: Context): RoomModel {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: buildDatabase(context).also {

                    INSTANCE = it
                }
            }
        }

        // TODO Remove destructive migration
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context, RoomModel::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration().build()
    }

    abstract fun goalsDao(): Goal.Dao
}