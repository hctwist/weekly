package com.twisthenry8gmail.weeklyphoenix.util.bundles

import android.os.Bundle

object TaskDateBundle {

    private const val TASK_DATE = "task_date"

    operator fun invoke(date: Long): Bundle {

        return Bundle().apply {

            putLong(TASK_DATE, date)
        }
    }

    fun extractDate(bundle: Bundle?): Long {

        if (bundle?.containsKey(TASK_DATE) != true) throw IllegalArgumentException("This must have a task date passed through it's arguments")
        return bundle.getLong(TASK_DATE)
    }

}