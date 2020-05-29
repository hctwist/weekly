package com.twisthenry8gmail.weeklyphoenix.util

import android.app.DatePickerDialog
import android.content.Context
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object DateTimeUtil {

    fun displayDate(localDate: LocalDate): String {

        return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(localDate)
    }

    fun displayDate(epochDay: Long): String {

        return displayDate(LocalDate.ofEpochDay(epochDay))
    }
}