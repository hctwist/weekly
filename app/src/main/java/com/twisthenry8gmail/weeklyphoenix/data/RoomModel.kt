package com.twisthenry8gmail.weeklyphoenix.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalHistory
import com.twisthenry8gmail.weeklyphoenix.data.tasks.Task

// RELEASE Reset to 1
@Database(entities = [Goal::class, GoalHistory::class, Task::class], version = 8)
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

        // RELEASE Remove destructive migration
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context, RoomModel::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration().build()
    }

    abstract fun goalsDao(): Goal.Dao

    abstract fun goalHistoryDao(): GoalHistory.Dao

    abstract fun tasksDao(): Task.Dao
}