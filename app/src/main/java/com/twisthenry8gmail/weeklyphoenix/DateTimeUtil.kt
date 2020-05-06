package com.twisthenry8gmail.weeklyphoenix

import android.app.DatePickerDialog
import android.content.Context
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object DateTimeUtil {

    val goalTimeFormatter = DecimalFormat("00")

    fun displayDate(epochDay: Long): String {

        return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            .format(LocalDate.ofEpochDay(epochDay))
    }

    fun showDatePickerDialog(context: Context, epochDay: Long, listener: (Long) -> Unit) {

        val localDate = LocalDate.ofEpochDay(epochDay)

        DatePickerDialog(context, { _, year, month, dayOfMonth ->

            listener(
                localDate.withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth)
                    .toEpochDay()
            )
        }, localDate.year, localDate.monthValue - 1, localDate.dayOfMonth).show()
    }

    fun showGoalTime(context: Context, seconds: Long): String {

        val totalMinutes = seconds / 60
        val hours = totalMinutes / 60
        return context.getString(
            R.string.time_pattern,
            goalTimeFormatter.format(hours),
            goalTimeFormatter.format(totalMinutes - hours * 60)
        )
    }
}