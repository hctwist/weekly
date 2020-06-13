package com.twisthenry8gmail.weeklyphoenix.data

import androidx.lifecycle.LiveData
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val date: Long,
    val complete: Boolean
) {

    @androidx.room.Dao
    interface Dao {

        @Insert
        suspend fun insert(task: Task)

        @Query("SELECT * FROM Task WHERE date < :now AND complete = 0")
        fun getOverdue(now: Long): LiveData<List<Task>>

        @Query("SELECT * FROM Task WHERE date = :epochDay")
        fun getFor(epochDay: Long): LiveData<List<Task>>

        @Query("SELECT * FROM Task WHERE date >= :fromEpoch")
        fun getFrom(fromEpoch: Long): LiveData<List<Task>>

        @Query("UPDATE Task SET complete = :complete WHERE id = :id")
        suspend fun setComplete(id: Int, complete: Boolean)
    }
}