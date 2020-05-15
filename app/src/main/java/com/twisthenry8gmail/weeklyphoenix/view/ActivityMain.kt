package com.twisthenry8gmail.weeklyphoenix.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.twisthenry8gmail.weeklyphoenix.util.NotificationHelper
import com.twisthenry8gmail.weeklyphoenix.R

class ActivityMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        NotificationHelper.createChannels(this)
    }
}