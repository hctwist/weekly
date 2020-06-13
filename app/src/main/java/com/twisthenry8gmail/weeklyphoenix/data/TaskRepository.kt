package com.twisthenry8gmail.weeklyphoenix.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class TaskRepository(private val tasksDao: Task.Dao) {

    fun getDay(epochDay: Long): LiveData<TaskDay> {

        return Transformations.map(tasksDao.getFor(epochDay)) {

            TaskDay(epochDay, it)
        }
    }

    fun getAllDays(): LiveData<List<TaskDay>> {

        val now = LocalDate.now()
        val todayEpoch = now.toEpochDay()

        val tasks = tasksDao.getFrom(todayEpoch)

        return Transformations.map(tasks) { t ->

            val taskDays = ArrayList<TaskDay>(7)

            for (i in 0L..6L) {

                val date = now.plusDays(i)
                val dateEpoch = date.toEpochDay()
                val datedTasks = t.filter { it.date == dateEpoch }

                taskDays.add(TaskDay(dateEpoch, datedTasks))
            }

            taskDays
        }
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