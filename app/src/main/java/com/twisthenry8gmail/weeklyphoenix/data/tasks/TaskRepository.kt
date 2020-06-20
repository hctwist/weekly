package com.twisthenry8gmail.weeklyphoenix.data.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.twisthenry8gmail.weeklyphoenix.AggregatorLiveData
import java.time.LocalDate

class TaskRepository(private val tasksDao: Task.Dao) {

    fun getAllFor(epochDay: Long): LiveData<List<Task>> {

        return tasksDao.getAllFor(epochDay)
    }

    fun getLimitedSnapshotsBetween(
        limit: Int,
        from: LocalDate,
        to: LocalDate
    ): LiveData<List<TaskSnapshot>> {

        val fromEpoch = from.toEpochDay()
        val toEpoch = to.toEpochDay()
        return tasksDao.getLimitedIncompleteBetween(limit, fromEpoch, toEpoch).map { tasks ->

            List(from.until(to).days + 1) { dayOffset ->

                val date = from.plusDays(dayOffset.toLong()).toEpochDay()
                TaskSnapshot(date, tasks.filter { it.date == date })
            }
        }
    }

    fun getOverdue(limit: Int): LiveData<List<Task>> {

        return tasksDao.getOverdue(LocalDate.now().toEpochDay(), limit)
    }

    fun getOverdue(): LiveData<List<Task>> {

        return tasksDao.getOverdue(LocalDate.now().toEpochDay())
    }

    suspend fun setComplete(taskId: Int, complete: Boolean) {

        tasksDao.setComplete(taskId, complete)
    }

    suspend fun add(task: Task) {

        tasksDao.insert(task)
    }
}