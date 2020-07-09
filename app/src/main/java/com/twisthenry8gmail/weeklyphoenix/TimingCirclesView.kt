package com.twisthenry8gmail.weeklyphoenix

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class TimingCirclesView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val linePaint = Paint().apply {

        isAntiAlias = true
        strokeWidth = 20F
        style = Paint.Style.STROKE
    }

    private val nCircles = 3

    private val circles = List(nCircles) { Circle() }

    init {

        start()
    }

    fun start() {

        ValueAnimator.ofFloat(0F, 1F).apply {

            interpolator = LinearInterpolator()
            duration = Long.MAX_VALUE
            addUpdateListener {

                circles.forEach {

                    val time = System.currentTimeMillis()
                    it.update(time)
                    invalidate()
                }
            }
            start()
        }
    }

    fun setColor(color: Int) {

        linePaint.color = color
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        val circleBounds = RectF(0F, 0F, w.toFloat(), h.toFloat())

        val strokeWidth = linePaint.strokeWidth / 2
        circleBounds.inset(strokeWidth, strokeWidth)

        circles.forEach {

            it.onBoundsChanged(circleBounds)
        }
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            circles.forEach {

                c.drawCircle(it.cx, it.cy, it.radius, linePaint)
            }
        }
    }

    private class Circle {

        private var bounds = RectF()

        private var orbitRadius = 0F

        var radius = 0F

        var cx = 0F
        var cy = 0F

        private val orbitTime = Random.nextLong(MIN_ORBIT_TIME, MAX_ORBIT_TIME)

        fun onBoundsChanged(bounds: RectF) {

            this.bounds.set(bounds)

            val minDim = min(bounds.width(), bounds.height())

            orbitRadius = minDim * Random.nextFloat() * ORBIT_RADIUS_JITTER_FRACTION / 2
            radius = (minDim / 2 - orbitRadius) * (1 - Random.nextFloat() * RADIUS_JITTER_FRACTION)
        }

        fun update(currentMillis: Long) {

            val angle = (((currentMillis % orbitTime).toFloat() / orbitTime) * 2 * PI).toFloat()

            cx = bounds.centerX() + orbitRadius * sin(angle)
            cy = bounds.centerY() + orbitRadius * cos(angle)
        }

        companion object {

            const val ORBIT_RADIUS_JITTER_FRACTION = 0.1F

            const val RADIUS_JITTER_FRACTION = 0.4F

            const val MIN_ORBIT_TIME = 10000L
            const val MAX_ORBIT_TIME = 30000L
        }
    }
}