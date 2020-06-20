package com.twisthenry8gmail.weeklyphoenix.data.goals

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.room.*
import com.twisthenry8gmail.weeklyphoenix.R
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @field:TypeConverters(Goal::class) val type: Type,
    val title: String,
    val progress: Long,
    val target: Long,
    @Embedded val reset: Reset,
    val resetDate: Long,
    val increase: Long,
    val increasePaused: Boolean,
    val startDate: Long,
    val endDate: Long,
    val color: Int,
    val sortOrder: Int
) {

    fun isComplete() = wouldBeComplete(this, progress)

    fun hasStarted(now: LocalDate) = !LocalDate.ofEpochDay(startDate).isAfter(now)

    fun hasEnded(now: LocalDate) =
        if (!hasEndDate()) false else !LocalDate.ofEpochDay(endDate).isAfter(now)

    fun hasEndDate() = hasEndDate(endDate)

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

            return Reset(
                multiple * n,
                unit
            )
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

        fun toReset() = Reset(
            multiple,
            unit
        )
    }

    @androidx.room.Dao
    interface Dao {

        @Query("SELECT * FROM Goal")
        fun getAll(): LiveData<List<Goal>>

        @Query("SELECT title FROM Goal")
        fun getTitles(): LiveData<List<String>>

        @Query("SELECT * FROM Goal WHERE resetDate > 0 AND resetDate <= :threshold")
        suspend fun getAllThatRequireReset(threshold: Long): List<Goal>

        @Query("SELECT IFNULL(MAX(sortOrder) + 1, 0) FROM Goal")
        suspend fun getNewSortOrder(): Int

        @Insert
        suspend fun insert(goal: Goal)

        @Transaction
        suspend fun insert(newGoal: NewGoal) {

            val x = getNewSortOrder()
            insert(newGoal.buildGoal(getNewSortOrder()))
        }

        @Query("UPDATE Goal SET sortOrder = :newSortOrder WHERE id = :goalId")
        suspend fun updateSortOrder(goalId: Int, newSortOrder: Int)

        @Query("UPDATE Goal SET sortOrder = sortOrder + 1 WHERE sortOrder >= :fromInc AND sortOrder < :toExc")
        suspend fun incrementSortOrdersBetween(fromInc: Int, toExc: Int)

        @Query("UPDATE Goal SET sortOrder = sortOrder - 1 WHERE sortOrder > :fromExc AND sortOrder <= :toInc")
        suspend fun decrementSortOrdersBetween(fromExc: Int, toInc: Int)

        @Query("SELECT sortOrder FROM Goal WHERE id = :goalId")
        suspend fun getSortOrderOf(goalId: Int): Int

        @Transaction
        suspend fun changeSortOrder(goalId: Int, newSortOrder: Int) {

            val oldSortOrder = getSortOrderOf(goalId)

            if (newSortOrder < oldSortOrder) {
                incrementSortOrdersBetween(newSortOrder, oldSortOrder)
                updateSortOrder(goalId, newSortOrder)
            } else {
                decrementSortOrdersBetween(oldSortOrder, newSortOrder)
                updateSortOrder(goalId, newSortOrder)
            }
        }

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

        fun hasEndDate(endDate: Long) = endDate >= 0

        fun wouldBeComplete(goal: Goal, progress: Long): Boolean {

            return progress >= goal.target
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