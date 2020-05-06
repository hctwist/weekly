package com.twisthenry8gmail.weeklyphoenix

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity
class Goal(
    @field:TypeConverters(Goal::class) val type: Type,
    @PrimaryKey var name: String,
    var target: Long,
    var resetMultiple: Long,
    @field:TypeConverters(Goal::class) var resetUnit: ChronoUnit,
    var resetDate: Long,
    val increase: Long,
    var startDate: Long,
    var endDate: Long,
    var color: Int
) {

    var current = 0L

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
    }

    enum class Type(@StringRes val displayNameRes: Int) {

        COUNTED(R.string.goal_counted), TIMED(R.string.goal_timed);

        fun getDisplayName(context: Context) = context.getString(displayNameRes)
    }

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

        @Insert
        suspend fun addGoal(goal: Goal)

        @Query("UPDATE Goal SET current = :current WHERE name = :goalName")
        suspend fun updateGoalProgress(goalName: String, current: Long)
    }
}