package com.twisthenry8gmail.weeklyphoenix.data

import androidx.lifecycle.LiveData
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query

@Entity(primaryKeys = ["startDate", "endDate", "goalId"])
class GoalHistory(
    val startDate: Long,
    val endDate: Long,
    val goalId: Int,
    val progress: Long,
    val target: Long
) {

    @androidx.room.Dao
    interface Dao {

        @Query("SELECT * FROM GoalHistory WHERE goalId = :goalId")
        fun getAllFor(goalId: Int): LiveData<List<GoalHistory>>

        @Insert
        suspend fun insert(goalHistory: GoalHistory)

        @Insert
        suspend fun insert(historys: List<GoalHistory>)
    }
}