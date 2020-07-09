package com.twisthenry8gmail.weeklyphoenix.data.goals

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import java.time.LocalDate

class GoalRepository(
    private val goalsDao: Goal.Dao,
    private val goalPreferences: SharedPreferences
) {

    private val goalSnapshots: LiveData<List<GoalSnapshot>>? = null

    suspend fun getAllThatRequireReset(): List<Goal> {

        return goalsDao.getAllThatRequireReset(LocalDate.now().toEpochDay())
    }

    suspend fun reset(goals: List<Goal>) {

        val resetUpdates = ArrayList<Goal.Dao.ResetUpdate>()
        val now = LocalDate.now()
        goals.forEach {

            val resetDate = LocalDate.ofEpochDay(it.resetDate)
            val periodsPassed = resetDate.until(now)[it.reset.unit] / it.reset.multiple

            val nextResetDate = Goal.getResetDateFrom(resetDate, it.reset * (periodsPassed + 1))
            var newTarget = it.target

            if (!it.increasePaused) {

                newTarget += (periodsPassed + 1) * it.increase
            }

            resetUpdates.add(Goal.Dao.ResetUpdate(it.id, nextResetDate, newTarget))
        }

        goalsDao.reset(resetUpdates)
    }

    suspend fun changeSortOrder(goalId: Int, newSortOrder: Int) {

        goalsDao.changeSortOrder(goalId, newSortOrder)
    }

    fun getSnapshots(): LiveData<List<GoalSnapshot>> {

        return goalSnapshots ?: goalsDao.getSnapshots()
    }

    fun getTitles(): LiveData<List<String>> {

        return goalSnapshots?.map { list -> list.map { it.title } } ?: goalsDao.getTitles()
    }

    fun get(id: Int): LiveData<Goal> {

        return goalsDao.get(id)
    }

    suspend fun add(newGoal: NewGoal) {

        goalsDao.insert(newGoal)
    }

    suspend fun addProgress(id: Int, progress: Long) {

        goalsDao.incrementProgress(id, progress)
    }

    suspend fun pauseIncrease(id: Int) {

        goalsDao.pauseIncrease(id)
    }

    suspend fun delete(id: Int) {

        goalsDao.delete(id)
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

        val id = goalPreferences.getInt(PREF_TIMED_GOAL_ID, -1)
        return goalsDao.get(id)
    }

    fun getTimingGoalStartTime(): Long {

        return goalPreferences.getLong(PREF_TIMED_GOAL_START, 0)
    }

    companion object {

        private const val PREF_TIMED_GOAL_ID = "timed_goal_name"
        private const val PREF_TIMED_GOAL_START = "timed_goal_start"
    }
}