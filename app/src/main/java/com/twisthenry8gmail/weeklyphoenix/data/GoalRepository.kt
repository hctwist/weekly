package com.twisthenry8gmail.weeklyphoenix.data

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import java.time.LocalDate

class GoalRepository(
    private val goalsDao: Goal.Dao,
    private val goalPreferences: SharedPreferences
) {

    companion object {

        private const val PREF_TIMED_GOAL_ID = "timed_goal_name"
        private const val PREF_TIMED_GOAL_START = "timed_goal_start"
    }

    private var goals: LiveData<List<Goal>>? = null

    suspend fun resetAllRequired() {

        val resetUpdates = ArrayList<Goal.Dao.ResetUpdate>()
        val now = LocalDate.now()

        goalsDao.getAllThatRequireReset(now.toEpochDay()).forEach {

            val resetDate = LocalDate.ofEpochDay(it.resetDate)
            it.updateResetDate(LocalDate.ofEpochDay(it.resetDate))

            if (!it.increasePaused) {

                val periodsPassed = resetDate.until(now)[it.resetUnit] / it.resetMultiple
                it.target += periodsPassed * it.increase
            }

            resetUpdates.add(Goal.Dao.ResetUpdate(it.name, it.resetDate, it.target))
        }

        // TODO Test
        goalsDao.resetGoals(resetUpdates)
    }

    fun getNames(): LiveData<List<String>> {

        if (goals == null) return goalsDao.getNames()
        return Transformations.map(goals!!) { g ->

            g.map { it.name }
        }
    }

    fun getAll(): LiveData<List<Goal>> {

        if (goals == null) goals = goalsDao.getAll()
        return goals!!
    }

    suspend fun add(goal: Goal) {

        goalsDao.addGoal(goal)
    }

    suspend fun addProgress(id: Int, progress: Long) {

        goalsDao.addGoalProgress(id, progress)
    }

    suspend fun pauseIncrease(goalName: String, pause: Boolean) {

        goalsDao.pauseGoalIncrease(goalName, pause)
    }

    suspend fun delete(goalName: String) {

        goalsDao.deleteGoal(goalName)
    }

    fun startTimer(id: Int): Long {

        val time = System.currentTimeMillis()
        goalPreferences.edit().run {

            putInt(PREF_TIMED_GOAL_ID, id)
            putLong(PREF_TIMED_GOAL_START, time)
            apply()
        }

        return time
    }

    fun stopTimer() {

        goalPreferences.edit().run {

            remove(PREF_TIMED_GOAL_ID)
            remove(PREF_TIMED_GOAL_START)
            apply()
        }
    }

    fun isTiming(): Boolean {

        return goalPreferences.contains(PREF_TIMED_GOAL_ID)
    }

    fun getTimingGoal(): LiveData<Goal> {

        val data: LiveData<Goal>

        val id = goalPreferences.getInt(PREF_TIMED_GOAL_ID, -1)
        val cachedGoal = goals?.value?.find { it.id == id }

        if (cachedGoal == null) {

            data = goalsDao.findGoal(id)
        } else {

            data = MutableLiveData<Goal>().apply {

                value = cachedGoal
            }
        }

        return data
    }

    fun getTimingGoalStartTime(): Long {

        return goalPreferences.getLong(PREF_TIMED_GOAL_START, 0)
    }
}