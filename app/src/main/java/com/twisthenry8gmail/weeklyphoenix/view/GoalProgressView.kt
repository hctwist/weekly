package com.twisthenry8gmail.weeklyphoenix.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.math.MathUtils
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.util.ColorUtil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

class GoalProgressView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val linePaint = Paint().apply {

        isAntiAlias = true
        strokeWidth = context.resources.getDimension(R.dimen.goal_progress_stroke)
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }

    private val thumbPaint = Paint().apply {


    }

    private val thumbRadius = context.resources.getDimension(R.dimen.goal_progress_thumb_radius)

    private val linePath = Path()
    private val linePathMeasure = PathMeasure()
    private val linePointPosAlloc = FloatArray(2)

    private var animating = false

    private val currentRandomState = SeededRandomState()
    private val previousRandomState = SeededRandomState()
    private val randomStateAlloc = RandomState()
    private var lineAnimationFraction = 0F

    private var animatedThumbFraction = 0F
    private var thumbFraction = 0F

    fun setSeed(seed: Long) {

        currentRandomState.seed = seed
    }

    fun setLineColor(color: Int) {

        linePaint.color = color
    }

    fun setThumbColor(color: Int) {

        thumbPaint.color = color
    }

    fun initialise(goal: Goal) {

        setLineColor(ColorUtil.lightenGoalColor(goal.color))
        setThumbColor(goal.color)
        updateProgress(goal)
    }

    fun updateProgress(goal: Goal, animate: Boolean = false) {

        val newThumbFraction = goal.progress.toFloat() / goal.target

        if (animate) {

            val newSeed = goal.resetDate + goal.progress

            val thumbStart = thumbFraction
            val thumbEnd = newThumbFraction

            animatedThumbFraction = thumbStart
            thumbFraction = thumbEnd

            previousRandomState.copy(currentRandomState)

            currentRandomState.seed = newSeed
            generateState(currentRandomState, width, height)
            lineAnimationFraction = 0F

            animating = true

            ValueAnimator.ofFloat(0F, 1F).run {

                duration = 1000
                interpolator = FastOutSlowInInterpolator()

                addUpdateListener {

                    val value = it.animatedValue as Float
                    animatedThumbFraction = thumbStart + (thumbEnd - thumbStart) * value
                    lineAnimationFraction = value
                    invalidate()
                }

                doOnEnd {

                    animating = false
                }

                start()
            }
        } else {

            animating = false
            thumbFraction = newThumbFraction
            invalidate()
        }
    }

    private fun getLinePadding() = max(linePaint.strokeWidth / 2, thumbRadius)

    private fun generateState(randomState: SeededRandomState, w: Int, h: Int) {

        val linePadding = getLinePadding()
        randomState.generate(w - linePadding, h - linePadding)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val heightRatio = 0.8

        val heightSize = (MeasureSpec.getSize(widthMeasureSpec) * heightRatio).toInt()
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        generateState(currentRandomState, w, h)
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            val linePadding = getLinePadding()

            val drawRandomState = if (animating) {

                randomStateAlloc.setAsLerpBetween(
                    previousRandomState,
                    currentRandomState,
                    lineAnimationFraction
                )
                randomStateAlloc
            } else {

                currentRandomState
            }

            drawRandomState.apply {

                linePath.reset()
                linePath.moveTo(linePadding, height.toFloat() - linePadding)

                linePath.cubicTo(
                    startControlPoint.x,
                    startControlPoint.y,
                    midControlPointIn.x,
                    midControlPointIn.y,
                    midpoint.x,
                    midpoint.y
                )

                linePath.cubicTo(
                    midControlPointOut.x,
                    midControlPointOut.y,
                    endControlPoint.x,
                    endControlPoint.y,
                    width - linePadding,
                    linePadding
                )
            }
            c.drawPath(linePath, linePaint)

            val drawThumbFraction = if (animating) animatedThumbFraction else thumbFraction
            linePathMeasure.setPath(linePath, false)
            linePathMeasure.getPosTan(
                linePathMeasure.length * drawThumbFraction,
                linePointPosAlloc,
                null
            )
            c.drawCircle(linePointPosAlloc[0], linePointPosAlloc[1], thumbRadius, thumbPaint)
        }
    }

    open class RandomState {

        var midpoint = PointF()

        var startControlPoint = PointF()
        var midControlPointIn = PointF()
        var midControlPointOut = PointF()
        var endControlPoint = PointF()

        open fun copy(randomState: RandomState) {

            midpoint.set(randomState.midpoint)
            startControlPoint.set(randomState.startControlPoint)
            midControlPointIn.set(randomState.midControlPointIn)
            midControlPointOut.set(randomState.midControlPointOut)
            endControlPoint.set(randomState.endControlPoint)

        }

        fun setAsLerpBetween(rs1: RandomState, rs2: RandomState, fraction: Float) {

            midpoint.setAsLerpBetween(rs1.midpoint, rs2.midpoint, fraction)

            startControlPoint.setAsLerpBetween(
                rs1.startControlPoint,
                rs2.startControlPoint,
                fraction
            )
            midControlPointIn.setAsLerpBetween(
                rs1.midControlPointIn,
                rs2.midControlPointIn,
                fraction
            )
            midControlPointOut.setAsLerpBetween(
                rs1.midControlPointOut,
                rs2.midControlPointOut,
                fraction
            )
            endControlPoint.setAsLerpBetween(rs1.endControlPoint, rs2.endControlPoint, fraction)
        }

        private fun PointF.setAsLerpBetween(p1: PointF, p2: PointF, fraction: Float) {

            x = MathUtils.lerp(p1.x, p2.x, fraction)
            y = MathUtils.lerp(p1.y, p2.y, fraction)
        }
    }

    class SeededRandomState : RandomState() {

        var seed = 0L
            set(value) {

                field = value
                seed()
            }

        private var w = 0F
        private var h = 0F

        private var rand = Random(seed)

        private fun seed() {

            rand = Random(seed)
        }

        fun generate(width: Float, height: Float) {

            w = width
            h = height

            randomiseMidPoint()

            randomiseControlPoint(0F, h, 1, -1, startControlPoint)

            val midControlPointAngle = randomAngle()
            randomiseControlPoint(
                midpoint.x,
                midpoint.y,
                -1,
                1,
                midControlPointAngle,
                midControlPointIn
            )
            randomiseControlPoint(
                midpoint.x,
                midpoint.y,
                1,
                -1,
                midControlPointAngle,
                midControlPointOut
            )

            randomiseControlPoint(w, 0F, -1, 1, endControlPoint)
        }

        private fun randomiseMidPoint() {

            val x = rand.signedFloat() * MIDPOINT_JITTER * w
            val y = rand.signedFloat() * MIDPOINT_JITTER * h

            midpoint.set(w / 2 + x, h / 2 + y)
        }

        private fun randomiseControlPoint(
            startX: Float,
            startY: Float,
            xDirection: Int,
            yDirection: Int,
            angle: Float,
            point: PointF
        ) {

            val radius = randomRadius()

            val x = radius * cos(angle) * CONTROL_JITTER * w
            val y = radius * sin(angle) * CONTROL_JITTER * h

            point.set(startX + xDirection * x, startY + yDirection * y)
        }

        // xDirection and yDirection should be -1 or 1
        private fun randomiseControlPoint(
            startX: Float,
            startY: Float,
            xDirection: Int,
            yDirection: Int,
            point: PointF
        ) {

            randomiseControlPoint(startX, startY, xDirection, yDirection, randomAngle(), point)
        }

        private fun randomRadius(): Float {

            return CONTROL_MIN_RADIUS_FRACTION + rand.nextFloat() * (1 - CONTROL_MIN_RADIUS_FRACTION)
        }

        private fun randomAngle(): Float {

            val slice = rand.nextFloat() * 2 * ANGLE_EXTREME_RANGE
            return slice + if (slice > ANGLE_EXTREME_RANGE) PI_OVER_TWO / 2 else 0F
        }

        private fun Random.signedFloat() = nextFloat() * 2 - 1

        override fun copy(randomState: RandomState) {

            super.copy(randomState)
            if (randomState is SeededRandomState) seed = randomState.seed
        }

        companion object {

            const val CONTROL_JITTER = 0.4F
            const val MIDPOINT_JITTER = 0.1F

            const val CONTROL_MIN_RADIUS_FRACTION = 0.5F

            const val PI_OVER_TWO = Math.PI.toFloat() / 2
            const val ANGLE_EXTREME_RANGE = PI_OVER_TWO / 4
        }
    }
}