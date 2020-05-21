package com.twisthenry8gmail.weeklyphoenix.util

import android.app.DatePickerDialog
import android.content.Context
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object DateTimeUtil {

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


}