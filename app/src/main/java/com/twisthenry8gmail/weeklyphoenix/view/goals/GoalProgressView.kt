package com.twisthenry8gmail.weeklyphoenix.view.goals

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import com.twisthenry8gmail.weeklyphoenix.util.ColorUtil
import com.twisthenry8gmail.weeklyphoenix.util.setAsLerpBetween
import java.lang.IllegalArgumentException
import kotlin.math.max
import kotlin.random.Random

class GoalProgressView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val linePaint = Paint().apply {

        isAntiAlias = true
        strokeWidth = context.resources.getDimension(R.dimen.goal_progress_stroke)
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }

    private val thumbPaint = Paint().apply {

        isAntiAlias = true
        color = context.getColor(R.color.goal_progress_thumb_color)
    }

    private var goalId = 0

    private val thumbRadius = context.resources.getDimension(R.dimen.goal_progress_thumb_radius)
    private val thumbOffset = context.resources.getDimension(R.dimen.goal_progress_thumb_offset)

    private val linePath = Path()
    private val linePathMeasure = PathMeasure()
    private val linePointPosAlloc = FloatArray(2)

    private var animating = false

    private val lineRectAlloc = RectF()
    private val nPoints = 5
    private val currentRandomState =
        SeededRandomState(
            nPoints
        )
    private val previousRandomState =
        SeededRandomState(
            nPoints
        )
    private val randomStateAlloc =
        RandomState(
            nPoints
        )
    private var lineAnimationFraction = 0F

    private var animatedThumbFraction = 0F
    private var thumbFraction = 0F

    fun hasBeenInitialised() = goalId > 0

    fun initialise(goal: Goal, useLightColor: Boolean = true) {

        goalId = goal.id

        linePaint.color = if (useLightColor) ColorUtil.lightenGoalColor(goal.color) else goal.color
        updateProgress(goal.progress, goal.target)
    }

    fun updateProgress(progress: Long, target: Long, animate: Boolean = false) {

        val newThumbFraction = progress.toFloat() / target
        val majorSeed = goalId.toLong()
        val minorSeed = progress

        if (animate) {

            val thumbStart = thumbFraction
            val thumbEnd = newThumbFraction

            animatedThumbFraction = thumbStart
            thumbFraction = thumbEnd

            previousRandomState.clone(currentRandomState)

            currentRandomState.majorSeed = majorSeed
            currentRandomState.minorSeed = minorSeed
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

            currentRandomState.majorSeed = majorSeed
            currentRandomState.minorSeed = minorSeed
            generateState(currentRandomState, width, height)
            animating = false
            thumbFraction = newThumbFraction
            invalidate()
        }
    }

    private fun generateState(randomState: SeededRandomState, w: Int, h: Int) {

        if (w + h > 0) {

            val strokeRadius = linePaint.strokeWidth / 2
            val verticalOffset = max(strokeRadius, thumbRadius)

            lineRectAlloc.set(-strokeRadius, verticalOffset, w + strokeRadius, h - verticalOffset)
            randomState.generate(lineRectAlloc)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val heightRatio = 0.4F

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

                val firstPoint = points.first()
                linePath.moveTo(firstPoint.x, firstPoint.y)

                for (i in 0 until nPoints - 1) {

                    val nextPoint = points[i + 1]
                    val controlPoint = controlPoints[i]

                    linePath.cubicTo(
                        controlPoint.point1.x,
                        controlPoint.point1.y,
                        controlPoint.point2.x,
                        controlPoint.point2.y,
                        nextPoint.x,
                        nextPoint.y
                    )
                }
            }

            c.drawPath(linePath, linePaint)

            // TODO Try using x coordinate as opposed to distance along line
            val thumbOffsetFraction = 0.1F
            val drawThumbFraction =
                thumbOffsetFraction + (if (animating) animatedThumbFraction else thumbFraction) * (1 - 2 * thumbOffsetFraction)
            linePathMeasure.setPath(linePath, false)
            linePathMeasure.getPosTan(
                linePathMeasure.length * drawThumbFraction,
                linePointPosAlloc,
                null
            )
            c.drawCircle(linePointPosAlloc[0], linePointPosAlloc[1], thumbRadius, thumbPaint)
        }
    }

    open class RandomState(val nPoints: Int) {

        val points = Array(nPoints) { PointF() }
        val controlPoints = Array(nPoints - 1) { ControlPoint() }

        open fun clone(randomState: RandomState) {

            ensureBijectivityWith(randomState)

            for (i in points.indices) {

                points[i].set(randomState.points[i])
            }

            for (i in controlPoints.indices) {

                controlPoints[i].clone(randomState.controlPoints[i])
            }
        }

        fun setAsLerpBetween(rs1: RandomState, rs2: RandomState, fraction: Float) {

            ensureBijectivityWith(rs1, rs2)

            for (i in points.indices) {

                points[i].setAsLerpBetween(rs1.points[i], rs2.points[i], fraction)
            }

            for (i in controlPoints.indices) {

                controlPoints[i].setAsLerpBetween(
                    rs1.controlPoints[i],
                    rs2.controlPoints[i],
                    fraction
                )
            }
        }

        private fun ensureBijectivityWith(vararg states: RandomState) {

            states.forEach {

                if (nPoints != it.nPoints) {

                    throw IllegalArgumentException("Both states must have the same number of points")
                }
            }
        }

        class ControlPoint {

            val point1 = PointF()
            val point2 = PointF()

            fun clone(controlPoint: ControlPoint) {

                point1.set(controlPoint.point1)
                point2.set(controlPoint.point2)
            }

            fun setAsLerpBetween(cp1: ControlPoint, cp2: ControlPoint, fraction: Float) {

                point1.setAsLerpBetween(cp1.point1, cp2.point1, fraction)
                point2.setAsLerpBetween(cp1.point2, cp2.point2, fraction)
            }
        }
    }

    class SeededRandomState(nPoints: Int) : RandomState(nPoints) {

        var majorSeed = 0L
        var minorSeed = 0L

        fun generate(rect: RectF) {

            val majorRand = Random(majorSeed)
            val minorRand = Random(minorSeed)

            val w = rect.width()
            val h = rect.height()

            val xIncrement = w / (nPoints - 1)
            val minYJitter = h * MIN_MAJOR_Y_JITTER

            val minorJitter = MINOR_Y_JITTER * h

            points.first().set(
                rect.left,
                rect.top + majorRand.nextFloat() * h
            )
            for (i in 1 until points.size) {

                val x = rect.left + xIncrement * i

                val previousPoint = points[i - 1]

                val negativeYThreshold = previousPoint.y - minYJitter
                val positiveYThreshold = previousPoint.y + minYJitter

                val negativeRangeSize = max(0F, negativeYThreshold - rect.top)
                val positiveRangeSize = max(0F, rect.bottom - positiveYThreshold)
                val randY = majorRand.nextFloat() * (negativeRangeSize + positiveRangeSize)
                val y = if (randY <= negativeRangeSize) {

                    rect.top + randY
                } else {

                    rect.bottom - randY
                }

                points[i].set(x, y)
            }

            points.forEach {

                it.y =
                    (it.y + minorRand.signedFloat() * minorJitter).coerceIn(rect.top, rect.bottom)
            }

            for (i in 0 until points.size - 1) {

                val p1 = points[i]
                val p2 = points[i + 1]

                val midX = (p1.x + p2.x) / 2
                controlPoints[i].point1.set(midX, p1.y)
                controlPoints[i].point2.set(midX, p2.y)
            }
        }

        private fun Random.signedFloat() = nextFloat() * 2 - 1

        override fun clone(randomState: RandomState) {

            super.clone(randomState)
            if (randomState is SeededRandomState) {

                majorSeed = randomState.majorSeed
                minorSeed = randomState.minorSeed
            }
        }

        companion object {

            const val MIN_MAJOR_Y_JITTER = 0.3F
            const val MINOR_Y_JITTER = 0.2F
        }
    }

}