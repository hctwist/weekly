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
}