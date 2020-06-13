package com.twisthenry8gmail.weeklyphoenix.data

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.room.*
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.util.GoalPropertyUtil
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @field:TypeConverters(Goal::class) var type: Type,
    val title: String,
    val progress: Long,
    val target: Long,
    @Embedded val reset: Reset,
    val resetDate: Long,
    val increase: Long,
    val increasePaused: Boolean,
    val startDate: Long,
    val endDate: Long,
    val color: Int
) {

    fun isComplete() = progress >= target

    fun hasStarted(now: LocalDate) = !LocalDate.ofEpochDay(startDate).isAfter(now)

    fun hasEnded(now: LocalDate) =
        if (!hasEndDate()) false else !LocalDate.ofEpochDay(endDate).isAfter(now)

    fun hasEndDate() = GoalPropertyUtil.hasEndDate(endDate)

    fun withProgressIncrement(progressIncrement: Long) =
        copy(progress = progress + progressIncrement)

    enum class Type(
        @StringRes val displayNameRes: Int,
        val minIncrement: Long,
        @DrawableRes val actionIconRes: Int,
        @StringRes val descriptionRes: Int
    ) {

        COUNTED(
            R.string.goal_type_counted,
            1,
            R.drawable.round_plus_one_24,
            R.string.goal_type_counted_description
        ),
        TIMED(
            R.string.goal_type_timed,
            60 * 15,
            R.drawable.round_play_arrow_24,
            R.string.goal_type_timed_description
        );

        fun getDisplayName(context: Context) = context.getString(displayNameRes)

        fun getDescription(context: Context) = context.getString(descriptionRes)
    }

    class Reset(
        val multiple: Long,
        @field:TypeConverters(Goal::class) val unit: ChronoUnit
    ) {

        operator fun times(n: Long): Reset {

            return Reset(multiple * n, unit)
        }

        fun isNever() = multiple == 0L

        fun isPreset(resetPreset: ResetPreset): Boolean {

            return multiple == resetPreset.multiple && unit == resetPreset.unit
        }

        class UnitException : IllegalArgumentException("Invalid ChronoUnit used for goal")
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
        YEARLY(1, ChronoUnit.YEARS, R.string.goal_frequency_yearly),
        NEVER(0, ChronoUnit.DAYS, R.string.goal_frequency_never);

        fun toReset() = Reset(multiple, unit)
    }

    @androidx.room.Dao
    interface Dao {

        // RELEASE Remove
        @Query("UPDATE Goal SET progress = 0")
        suspend fun forceZeroProgress()

        @Query("SELECT * FROM Goal")
        fun getAll(): LiveData<List<Goal>>

        @Query("SELECT title FROM Goal")
        fun getTitles(): LiveData<List<String>>

        @Query("SELECT * FROM GOAL WHERE resetDate > 0 AND resetDate <= :threshold")
        suspend fun getAllThatRequireReset(threshold: Long): List<Goal>

        @Insert
        suspend fun insert(goal: Goal)

        @Query("SELECT * FROM Goal WHERE id = :id")
        fun get(id: Int): LiveData<Goal>

        @Query("UPDATE Goal SET progress = progress + :progress WHERE id = :id")
        suspend fun incrementProgress(id: Int, progress: Long)

        @Query("UPDATE Goal SET increasePaused = NOT increasePaused WHERE id = :id")
        suspend fun pauseIncrease(id: Int)

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

        fun getResetDateFrom(from: LocalDate, reset: Reset): Long {

            return if (reset.isNever()) -1 else from.plus(reset).toEpochDay()
        }

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

fun LocalDate.plus(reset: Goal.Reset): LocalDate = plus(reset.multiple, reset.unit)

fun LocalDate.minus(reset: Goal.Reset): LocalDate = minus(reset.multiple, reset.unit)