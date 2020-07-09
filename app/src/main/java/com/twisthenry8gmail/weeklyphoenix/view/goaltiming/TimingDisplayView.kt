package com.twisthenry8gmail.weeklyphoenix.view.goaltiming

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import com.twisthenry8gmail.weeklyphoenix.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class TimingDisplayView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val linePaint = Paint().apply {

        isAntiAlias = true
        color = context.getColor(R.color.goal_progress_thumb_color)
        strokeWidth = resources.getDimension(R.dimen.goal_progress_stroke)
        strokeCap = Paint.Cap.ROUND
    }

    private val thumbPaint = Paint().apply {

        isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        if (widthSize > heightSize) {

            super.onMeasure(heightMeasureSpec, heightMeasureSpec)
        } else {

            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        }
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            val cy = height.toFloat() / 2
            c.drawLine(0F, cy, width.toFloat(), cy, linePaint)
        }
    }

    @Deprecated("No longer used")
    private class Circle {

        var canvasW = 0F
        var canvasH = 0F

        var cx = 0F
        var cy = 0F
        var r = 0F

        fun onSizeChanged(w: Int, h: Int) {

            canvasW = w.toFloat()
            canvasH = h.toFloat()

            cx = canvasW / 2
            cy = (canvasH / 2) - ((canvasW * canvasW) / (4 * canvasH))
            r = sqrt(cx * cx + cy * cy)
        }

        private fun resolveRadius(inset: Float) = r - inset

        private fun getPlotPoint(angle: Float, radiusInset: Float, point: PointF) {

            val resR = resolveRadius(radiusInset)
            point.x = resR * sin(angle) + cx
            point.y = resR * cos(angle) + cy
        }

        fun getCanvasPoint(angle: Float, radiusInset: Float, point: PointF) {

            getPlotPoint(angle, radiusInset, point)

            point.x -= canvasW / 2
            point.y = resolveRadius(radiusInset) - (point.y - cy)
        }
    }
}