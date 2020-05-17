package com.twisthenry8gmail.weeklyphoenix.data

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.room.*
import com.twisthenry8gmail.weeklyphoenix.R
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.random.Random

@Entity
data class Goal(
    @field:TypeConverters(Goal::class) val type: Type,
    @PrimaryKey var name: String,
    var progress: Long,
    var target: Long,
    var resetMultiple: Long,
    @field:TypeConverters(Goal::class) var resetUnit: ChronoUnit,
    var resetDate: Long,
    var increase: Long,
    var startDate: Long,
    var endDate: Long,
    var color: Int
) {

    fun isComplete() = progress >= target

    fun setResetPreset(resetPreset: ResetPreset) {

        resetMultiple = resetPreset.resetMultiple
        resetUnit = resetPreset.resetUnit
    }

    fun updateResetDate(from: LocalDate) {

        resetDate = when (resetUnit) {

            ChronoUnit.DAYS -> from.plusDays(resetMultiple)
            ChronoUnit.MONTHS -> from.plusMonths(resetMultiple)
            ChronoUnit.YEARS -> from.plusYears(resetMultiple)
            else -> throw Exception("Invalid ChronoUnit used for goal")
        }.toEpochDay()
    }

    fun hasResetPreset(preset: ResetPreset): Boolean {

        return preset.resetMultiple == resetMultiple && preset.resetUnit == resetUnit
    }

    fun hasEndDate() = endDate >= 0

    enum class Type(@StringRes val displayNameRes: Int, val minIncrement: Long) {

        COUNTED(R.string.goal_counted, 1), TIMED(
            R.string.goal_timed, 60 * 15
        );

        fun getDisplayName(context: Context) = context.getString(displayNameRes)
    }

    enum class ResetPreset(
        val displayNameRes: Int,
        val resetMultiple: Long,
        val resetUnit: ChronoUnit
    ) {

        DAILY(R.string.goal_frequency_daily, 1, ChronoUnit.DAYS),
        WEEKLY(R.string.goal_frequency_weekly, 7, ChronoUnit.DAYS),
        FORTNIGHTLY(R.string.goal_frequency_fortnight, 14, ChronoUnit.DAYS),
        MONTHLY(R.string.goal_frequency_monthly, 1, ChronoUnit.MONTHS),
        YEARLY(R.string.goal_frequency_yearly, 1, ChronoUnit.YEARS)
    }

    @androidx.room.Dao
    interface Dao {

        @Query("SELECT * FROM Goal")
        fun getAll(): LiveData<List<Goal>>

        @Query("SELECT name FROM Goal")
        fun getNames(): LiveData<List<String>>

        @Query("SELECT * FROM GOAL WHERE resetDate <= :threshold")
        fun getAllThatRequireReset(threshold: Long): List<Goal>

        @Insert
        suspend fun addGoal(goal: Goal)

        @Query("SELECT * FROM Goal WHERE name = :name LIMIT 1")
        suspend fun findGoal(name: String): Goal

        @Query("UPDATE Goal SET progress = :progress WHERE name = :goalName")
        suspend fun updateGoalProgress(goalName: String, progress: Long)

        class ResetUpdate(val goalName: String, val newResetDate: Long, val newTarget: Long)

        @Query("UPDATE Goal SET resetDate = :newResetDate, progress = 0, target = :newTarget WHERE name = :goalName")
        suspend fun resetGoal(goalName: String, newResetDate: Long, newTarget: Long)

        @Transaction
        suspend fun resetGoals(resetUpdates: List<ResetUpdate>) {

            resetUpdates.forEach {

                resetGoal(it.goalName, it.newResetDate, it.newTarget)
            }
        }

        @Query("DELETE FROM Goal WHERE name = :name")
        suspend fun deleteGoal(name: String)
    }

    companion object {

        @TypeConverter
        @JvmStatic
        fun fromType(type: Type) = type.name

        @TypeConverter
        @JvmStatic
        fun toType(type: String) = Type.valueOf(type)

        @TypeConverter
        @JvmStatic
        fun fromResetUnit(unit: ChronoUnit) = unit.ordinal

        @TypeConverter
        @JvmStatic
        fun toResetUnit(ordinal: Int) = ChronoUnit.values()[ordinal]

        fun buildDefaultGoal(context: Context, type: Goal.Type): Goal {

            val target = when (type) {

                Type.COUNTED -> 1L
                Type.TIMED -> 15 * 60
            }

            val startDate = LocalDate.now()

            val cTypedArray = context.resources.obtainTypedArray(R.array.goal_colors)
            val color = cTypedArray.getColor(Random.nextInt(cTypedArray.length()), 0)
            cTypedArray.recycle()

            val preset = Goal.ResetPreset.WEEKLY
            val goal = Goal(
                type,
                "",
                0,
                target,
                preset.resetMultiple,
                preset.resetUnit,
                -1,
                0,
                startDate.toEpochDay(),
                -1,
                color
            )
            goal.updateResetDate(startDate)

            return goal
        }
    }
}