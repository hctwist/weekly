package com.twisthenry8gmail.weeklyphoenix.util

import android.view.View
import android.view.ViewPropertyAnimator

fun View.popIn(): ViewPropertyAnimator {

    val scale = 0.9F
    scaleX = scale
    scaleY = scale

    alpha = 0F

    return animate().alpha(1F).scaleY(1F).scaleX(1F).setDuration(150)
}