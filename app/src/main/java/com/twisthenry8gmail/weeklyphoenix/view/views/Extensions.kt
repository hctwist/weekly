package com.twisthenry8gmail.weeklyphoenix.view.views

import android.graphics.PointF
import com.google.android.material.math.MathUtils

fun PointF.setAsLerpBetween(p1: PointF, p2: PointF, fraction: Float) {

    x = MathUtils.lerp(p1.x, p2.x, fraction)
    y = MathUtils.lerp(p1.y, p2.y, fraction)
}