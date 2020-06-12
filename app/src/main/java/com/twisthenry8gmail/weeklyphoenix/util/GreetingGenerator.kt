package com.twisthenry8gmail.weeklyphoenix.util

import android.content.res.Resources
import com.twisthenry8gmail.weeklyphoenix.R
import java.time.LocalTime

object GreetingGenerator {

    fun generate(resources: Resources): String {

        val now = LocalTime.now()

        return resources.getString(
            when (now.hour) {

                in 4..11 -> R.string.greeting_morning
                in 12..17 -> R.string.greeting_afternoon
                in 18..21 -> R.string.greeting_evening
                else -> R.string.greeting_night
            }
        )
    }
}