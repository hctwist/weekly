package com.twisthenry8gmail.weeklyphoenix.data.tasks

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val dateCreated: Long,
    val title: String,
    val date: Long,
    val complete: Boolean
) {

    @androidx.room.Dao
    interface Dao {

        // TODO Performance
        @Query("SELECT * FROM Task a WHERE a.date BETWEEN :fromEpoch AND :toEpoch AND complete = 0 AND a.id IN (SELECT b.id FROM Task b WHERE a.date == b.date ORDER BY b.dateCreated LIMIT :limit)")
        fun getLimitedIncompleteBetween(
            limit: Int,
            fromEpoch: Long,
            toEpoch: Long
        ): LiveData<List<Task>>

        @Query("SELECT date, COUNT(*) count FROM Task WHERE date BETWEEN :fromEpoch AND :toEpoch AND complete = 1 GROUP BY date")
        fun getCompletedCountsBetween(fromEpoch: Long, toEpoch: Long): LiveData<List<DatedCount>>

        @Insert
        suspend fun insert(task: Task)

        @Query("SELECT * FROM Task WHERE date < :now AND complete = 0 ORDER BY date LIMIT :limit")
        fun getOverdue(now: Long, limit: Int): LiveData<List<Task>>

        @Query("SELECT * FROM Task WHERE date < :now AND complete = 0")
        fun getOverdue(now: Long): LiveData<List<Task>>

        @Query("SELECT * FROM Task WHERE date = :epochDay")
        fun getAllFor(epochDay: Long): LiveData<List<Task>>

        @Query("SELECT * FROM Task WHERE date >= :fromEpoch")
        fun getFrom(fromEpoch: Long): LiveData<List<Task>>

        @Query("SELECT * FROM Task WHERE date = :epochDay AND complete = 0")
        fun getIncompleteFor(epochDay: Long): LiveData<List<Task>>

        @Query("SELECT COUNT(*) FROM Task WHERE date = :epochDay AND complete = 1")
        fun getCompletedCountFor(epochDay: Long): LiveData<Int>

        @Query("UPDATE Task SET complete = :complete WHERE id = :id")
        suspend fun setComplete(id: Int, complete: Boolean)
    }

    class DatedCount(
        @ColumnInfo(name = "date") val date: Long,
        @ColumnInfo(name = "count") val completedCount: Int
    )
}