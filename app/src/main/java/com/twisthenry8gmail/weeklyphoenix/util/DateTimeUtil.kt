package com.twisthenry8gmail.weeklyphoenix.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object DateTimeUtil {

    private fun ld(epochDay: Long) = LocalDate.ofEpochDay(epochDay)

    fun displayMediumDate(localDate: LocalDate): String {


        return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(localDate)
    }

    fun displayMediumDate(epochDay: Long): String {

        return displayMediumDate(ld(epochDay))
    }

    fun displayShortDate(epochDay: Long): String {

        return DateTimeFormatter.ofPattern("dd/MM").format(ld(epochDay))
    }

    fun displayMonthAndYear(epochDay: Long): String {

        return DateTimeFormatter.ofPattern("MMM yy").format(ld(epochDay))
    }

    fun displayDay(epochDay: Long): String {

        return DateTimeFormatter.ofPattern("EEE").format(ld(epochDay))
    }
}