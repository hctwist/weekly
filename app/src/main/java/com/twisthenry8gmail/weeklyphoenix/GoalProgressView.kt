package com.twisthenry8gmail.weeklyphoenix

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import android.view.animation.Interpolator
import androidx.core.content.ContextCompat
import kotlin.math.roundToLong

class GoalProgressView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val backingArcPaint = Paint().apply {

        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = resources.getDimension(R.dimen.goal_progress_stroke_width)

        color = ContextCompat.getColor(context, R.color.goal_card_progress_backing)
    }

    private val arcPaint = Paint().apply {

        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = resources.getDimension(R.dimen.goal_progress_stroke_width)
    }

    var maxProgress = 10L
        set(value) {

            field = value
            updateProgress()
        }

    private var progress = 0L

    private var animationProgress = 0.0

    private val arcRect = RectF()

    private var animator: ValueAnimator? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        updateArcAllocations(w)
    }

    private fun updateArcAllocations(s: Int) {

        val strokeWidth = arcPaint.strokeWidth
        arcRect.set(strokeWidth, strokeWidth, s - strokeWidth, s - strokeWidth)
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            c.drawArc(arcRect, -90F, 360F, false, backingArcPaint)
            c.drawArc(arcRect, -90F, getSweepAngle(animationProgress), false, arcPaint)
        }
    }

    fun setColor(color: Int) {

        arcPaint.color = color
        invalidate()
    }

    fun setProgress(progress: Long, animate: Boolean = false) {

        this.progress = progress
        if (animate) animateProgress() else updateProgress()
    }

    private fun updateProgress() {

        animator?.cancel()
        animationProgress = progress.toDouble()
        invalidate()
    }

    private fun animateProgress() {

        animator?.cancel()

        val totalDuration = 3000

        animator = ValueAnimator.ofFloat(animationProgress.toFloat(), progress.toFloat()).apply {

            interpolator = chooseInterpolator()
            duration = (totalDuration * ((progress - animationProgress) / maxProgress)).roundToLong()
            addUpdateListener {

                animationProgress = (it.animatedValue as Float).toDouble()
                invalidate()
            }

            start()
        }
    }

    private fun chooseInterpolator(): Interpolator {

        return PathInterpolator(0.835F, -0.600F, 0.165F, 1.000F)
    }

    private fun getSweepAngle(p: Double): Float {

        return ((p / maxProgress) * 360).toFloat()
    }
}