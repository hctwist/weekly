package com.twisthenry8gmail.weeklyphoenix.data.goals

import androidx.lifecycle.LiveData
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query

@Entity(primaryKeys = ["goalId", "date"])
class GoalHistory(
    val goalId: Int,
    val date: Long,
    val progress: Long,
    val target: Long
) {

    @androidx.room.Dao
    interface Dao {

        @Query("SELECT * FROM GoalHistory WHERE goalId = :goalId AND date >= :fromDate")
        fun getAllFor(goalId: Int, fromDate: Long): LiveData<List<GoalHistory>>

        @Insert
        suspend fun insert(goalHistory: GoalHistory)

        @Insert
        suspend fun insert(historys: List<GoalHistory>)
    }
}