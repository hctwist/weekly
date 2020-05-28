package com.twisthenry8gmail.weeklyphoenix.data

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.room.*
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.util.GoalPropertyUtil
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// TODO Immutability
@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @field:TypeConverters(Goal::class) var type: Type,
    var title: String,
    var progress: Long,
    var target: Long,
    @Embedded var reset: Reset,
    var resetDate: Long,
    var increase: Long,
    var increasePaused: Boolean,
    var startDate: Long,
    var endDate: Long,
    var color: Int
) {

    fun isComplete() = progress >= target

    fun getResetDateFrom(from: LocalDate): Long {

        return when (reset.unit) {

            ChronoUnit.DAYS -> from.plusDays(reset.multiple)
            ChronoUnit.MONTHS -> from.plusMonths(reset.multiple)
            ChronoUnit.YEARS -> from.plusYears(reset.multiple)
            else -> throw Exception("Invalid ChronoUnit used for goal")
        }.toEpochDay()
    }

    @Deprecated("Immutability", replaceWith = ReplaceWith("getResetDateFrom"))
    fun updateResetDate(from: LocalDate) {

        resetDate = getResetDateFrom(from)
    }

    fun hasEndDate() = GoalPropertyUtil.hasEndDate(endDate)

    enum class Type(
        @StringRes val displayNameRes: Int,
        val minIncrement: Long,
        @DrawableRes val actionIconRes: Int
    ) {

        COUNTED(R.string.goal_type_counted, 1, R.drawable.round_plus_one_24),
        TIMED(R.string.goal_type_timed, 60 * 15, R.drawable.round_play_arrow_24);

        fun getDisplayName(context: Context) = context.getString(displayNameRes)
    }

    class Reset(
        val multiple: Long,
        @field:TypeConverters(Goal::class) val unit: ChronoUnit
    ) {

        fun isPreset(resetPreset: ResetPreset): Boolean {

            return multiple == resetPreset.multiple && unit == resetPreset.unit
        }
    }

    enum class ResetPreset(
        val multiple: Long,
        val unit: ChronoUnit,
        val displayNameRes: Int
    ) {

        DAILY(1, ChronoUnit.DAYS, R.string.goal_frequency_daily),
        WEEKLY(7, ChronoUnit.DAYS, R.string.goal_frequency_weekly),
        FORTNIGHTLY(14, ChronoUnit.DAYS, R.string.goal_frequency_fortnight),
        MONTHLY(1, ChronoUnit.MONTHS, R.string.goal_frequency_monthly),
        YEARLY(1, ChronoUnit.YEARS, R.string.goal_frequency_yearly);

        fun toReset() = Reset(multiple, unit)
    }

    @androidx.room.Dao
    interface Dao {

        @Query("SELECT * FROM Goal")
        fun getAll(): LiveData<List<Goal>>

        @Query("SELECT title FROM Goal")
        fun getTitles(): LiveData<List<String>>

        @Query("SELECT * FROM GOAL WHERE resetDate <= :threshold")
        suspend fun getAllThatRequireReset(threshold: Long): List<Goal>

        @Insert
        suspend fun insert(goal: Goal)

        @Query("SELECT * FROM Goal WHERE id = :id LIMIT 1")
        fun find(id: Int): LiveData<Goal>

        @Query("UPDATE Goal SET progress = progress + :progress WHERE id = :id")
        suspend fun incrementProgress(id: Int, progress: Long)

        @Query("UPDATE Goal SET increasePaused = :pause WHERE id = :id")
        suspend fun pauseIncrease(id: Int, pause: Boolean)

        class ResetUpdate(val id: Int, val newResetDate: Long, val newTarget: Long)

        @Query("UPDATE Goal SET resetDate = :newResetDate, progress = 0, target = :newTarget WHERE id = :id")
        suspend fun reset(id: Int, newResetDate: Long, newTarget: Long)

        @Transaction
        suspend fun reset(resetUpdates: List<ResetUpdate>) {

            resetUpdates.forEach {

                reset(it.id, it.newResetDate, it.newTarget)
            }
        }

        @Query("DELETE FROM Goal WHERE id = :id")
        suspend fun delete(id: Int)
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
    }
}