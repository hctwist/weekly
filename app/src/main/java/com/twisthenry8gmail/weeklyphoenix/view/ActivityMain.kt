package com.twisthenry8gmail.weeklyphoenix.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.twisthenry8gmail.weeklyphoenix.util.NotificationHelper
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.WeeklyApplication
import kotlinx.android.synthetic.main.activity_main.*

class ActivityMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        NotificationHelper.createChannels(this)
    }
}