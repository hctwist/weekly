package com.twisthenry8gmail.weeklyphoenix.data.tasks

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.twisthenry8gmail.weeklyphoenix.AggregatorLiveData
import com.twisthenry8gmail.weeklyphoenix.PreferenceLiveData
import java.time.LocalDate

class TaskRepository(
    private val tasksDao: Task.Dao,
    private val taskPreferences: SharedPreferences
) {

    fun getAllFor(epochDay: Long): LiveData<List<Task>> {

        return tasksDao.getAllFor(epochDay)
    }

    fun getLimitedSnapshotsBetween(
        limit: Int,
        from: LocalDate,
        to: LocalDate
    ): LiveData<List<TaskSnapshot>> {

        val now = LocalDate.now()
        val fromEpoch = from.toEpochDay()
        val toEpoch = to.toEpochDay()
        return tasksDao.getLimitedIncompleteBetween(limit, fromEpoch, toEpoch).map { tasks ->

            val capacity = from.until(to).days + 1
            val list = ArrayList<TaskSnapshot>(capacity)

            for (i in 0 until capacity) {

                val date = from.plusDays(i.toLong())
                val dateEpoch = date.toEpochDay()

                val matchingTasks = tasks.filter { it.date == dateEpoch }
                if (matchingTasks.isNotEmpty() || !date.isBefore(now)) {

                    list.add(TaskSnapshot(dateEpoch, matchingTasks))
                }
            }

            list
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
        setHasAddedTask()
    }

    fun setHasAddedTask() {

        taskPreferences.edit().putBoolean(PREF_HAS_ADDED_TASK, true).apply()
    }

    fun hasAddedTask(): LiveData<Boolean> {

        return PreferenceLiveData.Builder(taskPreferences, PREF_HAS_ADDED_TASK)
            .buildForBoolean(false)
    }

    companion object {

        const val PREF_HAS_ADDED_TASK = "has_added_task"
    }
}