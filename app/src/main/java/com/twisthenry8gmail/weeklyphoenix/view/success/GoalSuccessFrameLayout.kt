package com.twisthenry8gmail.weeklyphoenix.view.success

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.card.MaterialCardView
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import com.twisthenry8gmail.weeklyphoenix.util.ColorUtil
import com.twisthenry8gmail.weeklyphoenix.util.setAsLerpBetween
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

class GoalSuccessFrameLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val flyingPathPaint = Paint().apply {

        isAntiAlias = true
        strokeWidth = context.resources.getDimension(R.dimen.goal_progress_stroke)
        style = Paint.Style.STROKE
    }

    private val flyingThumbPaint = Paint().apply {

        isAntiAlias = true
        color = context.getColor(R.color.goal_progress_thumb_color)
    }

    private val flyingPaths = Array(20) {

        PathFlyingObject()
    }

    private val flyingThumb =
        ThumbFlyingObject(context.resources.getDimension(R.dimen.goal_progress_thumb_radius))

    private var animationProgress = 0F

    init {

        setWillNotDraw(false)
    }

    fun setGoal(goal: Goal) {

        getBox().setCardBackgroundColor(goal.color)
        flyingPathPaint.color = ColorUtil.lightenGoalColor(goal.color)
        startAnimation()
    }

    fun startAnimation() {

        getBox().animate().run {

            duration = 400
            scaleY(0F)
            scaleX(0F)

            withEndAction {

                val content = getChildAt(1)
                content.scaleX = 0.8F
                content.scaleY = 0.8F
                content.alpha = 0F

                content.animate().apply {

                    duration = 300

                    scaleX(1F)
                    scaleY(1F)
                    alpha(1F)
                }
            }
        }

        ValueAnimator.ofFloat(0F, 1F).run {

            duration = 1000
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener {

                animationProgress = it.animatedValue as Float
                invalidate()
            }

            start()
        }
    }

    private fun getBox() = getChildAt(0) as MaterialCardView

    private fun initialiseFlyingObjects() {

        val box = getBox()

        // TODO Take corners into account
        val drawBounds =
            RectF(box.left.toFloat(), box.top.toFloat(), box.right.toFloat(), box.bottom.toFloat())

        val minRadius = max(height + drawBounds.height(), width + drawBounds.width())
        val maxRadius = minRadius * 1.5F

        flyingPaths.forEach {

            initialiseObject(it, minRadius, maxRadius, drawBounds)
        }
        initialiseObject(flyingThumb, minRadius, maxRadius, drawBounds)
    }

    private fun initialiseObject(
        o: FlyingObject,
        minRadius: Float,
        maxRadius: Float,
        drawBounds: RectF
    ) {

        o.drawBounds = drawBounds

        val angle = Random.nextFloat() * 2 * Math.PI
        val radius = minRadius + Random.nextFloat() * (maxRadius - minRadius)

        o.setEndPoint((radius * sin(angle)).toFloat(), (radius * cos(angle)).toFloat())
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        super.onLayout(changed, left, top, right, bottom)
        initialiseFlyingObjects()
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let { c ->

            flyingPaths.forEach {

                it.draw(animationProgress, c, flyingPathPaint)
            }

            flyingThumb.draw(animationProgress, c, flyingThumbPaint)
        }
    }

    abstract class FlyingObject {

        var drawBounds: RectF = RectF()
            set(value) {

                field = value
                startPoint.set(drawBounds.centerX(), drawBounds.centerY())
                dirty = true
            }

        protected val startPoint = PointF()
        protected val endPoint = PointF()

        var dirty = false

        fun setEndPoint(x: Float, y: Float) {

            endPoint.set(x, y)
            dirty = true
        }

        fun draw(animationProgress: Float, canvas: Canvas, paint: Paint) {

            if (dirty) {

                initialise()
                dirty = false
            }
            onDraw(animationProgress, canvas, paint)
        }

        fun setToRandomInDrawRect(pointF: PointF) {

            val x = drawBounds.left + Random.nextFloat() * drawBounds.width()
            val y = drawBounds.top + Random.nextFloat() * drawBounds.height()

            pointF.set(x, y)
        }

        open fun initialise() {


        }

        abstract fun onDraw(animationProgress: Float, canvas: Canvas, paint: Paint)
    }

    class PathFlyingObject : FlyingObject() {

        private val master = Path()
        private val animated = Path()

        private val rotationMatrix = Matrix()

        private val pointF = PointF()

        override fun initialise() {

            master.reset()

            setToRandomInDrawRect(pointF)
            master.moveTo(pointF.x, pointF.y)

            setToRandomInDrawRect(pointF)
            val x1 = pointF.x
            val y1 = pointF.y
            setToRandomInDrawRect(pointF)
            master.quadTo(x1, y1, pointF.x, pointF.y)
        }

        override fun onDraw(animationProgress: Float, canvas: Canvas, paint: Paint) {

            rotationMatrix.setRotate(360 * animationProgress, startPoint.x, startPoint.y)
            master.transform(rotationMatrix, animated)

            pointF.setAsLerpBetween(startPoint, endPoint, animationProgress)
            animated.offset(pointF.x - startPoint.x, pointF.y - startPoint.y)

            canvas.drawPath(animated, paint)
        }
    }

    class ThumbFlyingObject(private val radius: Float) : FlyingObject() {

        private val pointF = PointF()

        override fun onDraw(animationProgress: Float, canvas: Canvas, paint: Paint) {

            pointF.setAsLerpBetween(startPoint, endPoint, animationProgress)
            canvas.drawCircle(pointF.x, pointF.y, radius, paint)
        }
    }
}