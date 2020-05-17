package com.twisthenry8gmail.weeklyphoenix.data

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import java.time.LocalDate

class GoalRepository(
    private val goalsDao: Goal.Dao,
    private val goalPreferences: SharedPreferences
) {

    companion object {

        private const val PREF_TIMED_GOAL_NAME = "timed_goal_name"
        private const val PREF_TIMED_GOAL_START = "timed_goal_start"
    }

    private var goals: LiveData<List<Goal>>? = null

    suspend fun resetAllRequired() {

        val resetUpdates = ArrayList<Goal.Dao.ResetUpdate>()
        val now = LocalDate.now()

        goalsDao.getAllThatRequireReset(now.toEpochDay()).forEach {

            val resetDate = LocalDate.ofEpochDay(it.resetDate)
            val periodsPassed = resetDate.until(now)[it.resetUnit] / it.resetMultiple

            it.updateResetDate(LocalDate.ofEpochDay(it.resetDate))
            resetUpdates.add(
                Goal.Dao.ResetUpdate(
                    it.name,
                    it.resetDate,
                    it.target + periodsPassed * it.increase
                )
            )
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

    suspend fun find(name: String): Goal {

        // TODO Use goals cache above
        return goalsDao.findGoal(name)
    }

    suspend fun add(goal: Goal) {

        goalsDao.addGoal(goal)
    }

    suspend fun updateGoalProgress(goalName: String, progress: Long) {

        goalsDao.updateGoalProgress(goalName, progress)
    }

    suspend fun delete(goalName: String) {

        goalsDao.deleteGoal(goalName)
    }

    fun startTimer(goalName: String): Long {

        val time = System.currentTimeMillis()
        goalPreferences.edit().run {

            putString(PREF_TIMED_GOAL_NAME, goalName)
            putLong(PREF_TIMED_GOAL_START, time)
            apply()
        }

        return time
    }

    fun stopTimer() {

        goalPreferences.edit().run {

            remove(PREF_TIMED_GOAL_NAME)
            remove(PREF_TIMED_GOAL_START)
            apply()
        }
    }

    fun isTiming(): Boolean {

        return goalPreferences.contains(PREF_TIMED_GOAL_NAME)
    }

    fun getTimingGoalName(): String? {

        return goalPreferences.getString(PREF_TIMED_GOAL_NAME, null)
    }

    fun getTimingGoalStartTime(): Long {

        return goalPreferences.getLong(PREF_TIMED_GOAL_START, 0)
    }
}