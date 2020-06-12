package com.twisthenry8gmail.weeklyphoenix.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class TaskRepository(private val tasksDao: Task.Dao) {

    fun getFor(epochDay: Long): LiveData<TaskDay> {

        val data = MutableLiveData<TaskDay>()

        val now = LocalDate.now()
        val date = LocalDate.ofEpochDay(epochDay)

        return object : MediatorLiveData<TaskDay>() {

            var current: List<Task>? = null
            var overdue: List<Task>? = null

            init {

                addSource(tasksDao.getFor(epochDay)) {

                    current = it
                    onSourceChanged()
                }

                if (now == date) {

                    addSource(tasksDao.getOverdue(now.toEpochDay())) {

                        overdue = it
                        onSourceChanged()
                    }
                } else {

                    overdue = listOf()
                }
            }

            fun onSourceChanged() {

                if (current != null && overdue != null) {

                    value = TaskDay(epochDay, current!!, overdue!!)
                }
            }
        }
    }

    fun getAllDays(): LiveData<List<TaskDay>> {

        val now = LocalDate.now()
        val todayEpoch = now.toEpochDay()

        val incompleteTasks = tasksDao.getAllActive(todayEpoch)

        return Transformations.map(incompleteTasks) { tasks ->

            val taskDays = ArrayList<TaskDay>(7)

            val todayTasks = tasks.filter { it.date <= todayEpoch }
            val partitionedTasks = todayTasks.partition { it.date == todayEpoch }
            taskDays.add(TaskDay(todayEpoch, partitionedTasks.first, partitionedTasks.second))

            for (i in 1L..6L) {

                val date = now.plusDays(i)
                val dateEpoch = date.toEpochDay()
                val datedTasks = tasks.filter { it.date == dateEpoch }

                taskDays.add(TaskDay(dateEpoch, datedTasks, listOf()))
            }

            taskDays
        }
    }

    suspend fun add(task: Task) {

        tasksDao.insert(task)
    }
}