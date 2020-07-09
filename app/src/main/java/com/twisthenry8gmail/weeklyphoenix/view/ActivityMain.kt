package com.twisthenry8gmail.weeklyphoenix.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.util.NotificationHelper
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication

class ActivityMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.AppTheme)

        setContentView(R.layout.activity_main)

        val controller =
            (supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment).navController
        controller.graph = controller.navInflater.inflate(R.navigation.main_nav_graph).apply {

            startDestination = chooseStartDestination()
        }

        NotificationHelper.createChannels(this)
    }

    private fun chooseStartDestination(): Int {

        return when {
            weeklyApplication().goalRepository.isTiming() -> R.id.fragmentGoalTimer
            weeklyApplication().mainRepository.isFirstTime() -> R.id.fragmentOnboarding
            else -> R.id.fragmentMain
        }
    }
}