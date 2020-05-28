package com.twisthenry8gmail.weeklyphoenix.util

import android.graphics.Color
import androidx.core.graphics.ColorUtils

object ColorUtil {

    fun lightenGoalColor(color: Int) = ColorUtils.blendARGB(color, Color.WHITE, 0.85F)
}