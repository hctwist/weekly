package com.twisthenry8gmail.weeklyphoenix.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.twisthenry8gmail.weeklyphoenix.R
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class DatelineDrawableView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val minSpacing = resources.getDimension(R.dimen.dateline_min_spacing)
    private val circleRadius = resources.getDimension(R.dimen.dateline_circle_radius)
    private val lineHeight = circleRadius / 4

    private val linePaint = Paint().apply {

        color = Color.LTGRAY
        strokeWidth = lineHeight
    }

    private val circlePaint = Paint().apply {

        isAntiAlias = true
        color = Color.LTGRAY
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(ceil(circleRadius * 2).roundToInt(), MeasureSpec.EXACTLY),
            heightMeasureSpec
        )
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            c.drawLine(circleRadius, 0F, circleRadius, height.toFloat(), linePaint)

            val nCircles =
                floor((height -  minSpacing) / (minSpacing + circleRadius * 2)).roundToInt()
            val spacing = (height - nCircles * circleRadius * 2) / (nCircles + 1)

            for (i in 0 until nCircles) {

                val start = spacing + ((circleRadius * 2 + spacing) * i) + circleRadius
                c.drawCircle(circleRadius, start, circleRadius, circlePaint)
            }
        }
    }
}