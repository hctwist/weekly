package com.twisthenry8gmail.weeklyphoenix.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.icu.util.Measure
import android.util.AttributeSet
import android.view.View
import com.twisthenry8gmail.weeklyphoenix.R
import kotlin.math.roundToInt

class GoalGraphView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val linePaint = Paint().apply {

        color = context.getColor(R.color.light_grey)
        strokeWidth = resources.getDimension(R.dimen.stroke_width)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthRatio = 0.5F

        val width = MeasureSpec.getSize(widthMeasureSpec)

        val newHeightSpec =
            MeasureSpec.makeMeasureSpec((width * widthRatio).roundToInt(), MeasureSpec.EXACTLY)

        super.onMeasure(widthMeasureSpec, newHeightSpec)
    }

    override fun onDraw(canvas: Canvas?) {


    }
}