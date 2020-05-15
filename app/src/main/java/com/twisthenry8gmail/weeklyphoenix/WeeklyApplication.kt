package com.twisthenry8gmail.weeklyphoenix

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.twisthenry8gmail.weeklyphoenix.data.RoomModel
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import kotlinx.coroutines.coroutineScope

class WeeklyApplication : Application() {

    lateinit var goalRepository: GoalRepository

    override fun onCreate() {
        super.onCreate()

        goalRepository = GoalRepository(
            RoomModel.getInstance(this).goalsDao(),
            getSharedPreferences("goal_pref", Context.MODE_PRIVATE)
        )


    }
}

fun Fragment.weeklyApplication(): WeeklyApplication {

    return requireActivity().application as WeeklyApplication
}