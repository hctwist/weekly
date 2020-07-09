package com.twisthenry8gmail.weeklyphoenix.util

import android.content.Context
import android.util.TypedValue
import androidx.core.graphics.ColorUtils

object ColorUtil {

    fun lightenColor(color: Int) = ColorUtils.setAlphaComponent(color, 50)

    fun compositeColorWithBackground(context: Context, color: Int): Int {

        val backgroundColor = resolveColorAttribute(context, android.R.attr.colorBackground)
        return ColorUtils.compositeColors(color, backgroundColor)
    }

    fun resolveColorAttribute(context: Context, colorAttr: Int): Int {

        val typedValue = TypedValue()
        context.theme.resolveAttribute(colorAttr, typedValue, true)

        return context.getColor(if (typedValue.resourceId == 0) typedValue.data else typedValue.resourceId)
    }
}