package com.twisthenry8gmail.weeklyphoenix.view.viewtasks

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.graphics.ColorUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.math.MathUtils.lerp
import com.twisthenry8gmail.weeklyphoenix.R
import kotlin.math.*

// TODO Rtl
class TaskRowView(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {

    var onCheckChangedListener: OnCheckedChangeListener? = null

    private var checked = false

    private var strikeProgress = 0F
    private var textOffset = 0F
    private var tickProgress = 0F

    private val lineBounds = LineBounds()

    private val textView: TextView

    private val dashColor = context.getColor(R.color.task_row_view_dash)
    private val strikeColor = context.getColor(R.color.task_row_view_strike)
    private val textColor: Int

    private val tickDrawable = context.getDrawable(R.drawable.round_done_24)!!.mutate().apply {

        setTint(dashColor)
    }

    private val linePaint = Paint().apply {

        color = dashColor
        strokeWidth = resources.getDimension(R.dimen.task_row_view_dash_stroke_width)
    }

    private val tickSize = resources.getDimension(R.dimen.task_row_view_tick_size)
    private val dashSize = resources.getDimension(R.dimen.task_row_view_dash_size)
    private val textPadding = resources.getDimension(R.dimen.task_row_view_text_padding)

    private val checkAreaSize = max(dashSize, tickSize)

    private val maxTickSize: Float

    private var strikeAnimator: ValueAnimator? = null
    private val baseStrikeAnimationDuration = 500L

    private var tickAnimator: ValueAnimator? = null
    private val tickInterpolatorTension = 2F
    private val tickInterpolator = OvershootInterpolator(tickInterpolatorTension)
    private val tickAnimationOffset = 200L
    private val tickAnimationDuration = 300L

    private var touchedCheckArea = false

    private val checkDragThresholdX =
        context.resources.getDimension(R.dimen.task_row_checked_threshold)
    private val maxDragTranslateX = context.resources.getDimension(R.dimen.task_row_max_translate_x)
    private var dragSettling = false
    private var dragStartX = 0F
    private val dragTranslationInterpolator = DecelerateInterpolator()
    private val baseDragSettleAnimationDuration = 200L

    init {

        setWillNotDraw(false)
        clipChildren = false
        clipToPadding = false

        View.inflate(
            context,
            R.layout.task_row_view, this
        )

        textView = getChildAt(0) as TextView
        textColor = textView.textColors?.defaultColor ?: Color.RED

        val t = tickInterpolatorTension
        // Maximum point of the interpolation curve
        val maxTickSizeX = (t + 3) / (3 * (t + 1))
        maxTickSize = tickInterpolator.getInterpolation(maxTickSizeX) * tickSize
    }

    fun setText(text: CharSequence) {

        textView.text = text
        lineBounds.dirty = true
    }

    fun setChecked(checked: Boolean) {

        if (this.checked != checked) {

            registerChecked(checked)

            val newProgress = if (checked) 1F else 0F
            tickProgress = newProgress
            setStrikeAnimationProgress(newProgress)
        }
    }

    fun registerChecked(checked: Boolean) {

        this.checked = checked
        onCheckChangedListener?.onCheckChanged(checked)
    }

    fun animateChecked(checked: Boolean) {

        if (this.checked != checked) {

            registerChecked(checked)

            animateStrikeTo(if (checked) 1F else 0F)
            if (checked) {

                showTick(tickAnimationOffset)
            } else {

                hideTick()
            }
        }
    }

    fun setStrikeAnimationProgress(progress: Float) {

        strikeProgress = progress
        invalidateTextColor()
        invalidateDashColor()
        invalidate()
    }

    fun setTextOffset(offset: Float) {

        textOffset = offset
        textView.translationX = textOffset
        invalidate()
    }

    fun showTick(startDelay: Long = 0) {

        tickAnimator?.cancel()

        if (tickAnimator == null) initialiseTickAnimator()

        tickAnimator?.let {

            it.startDelay = startDelay
            it.setFloatValues(tickProgress, 1F)
            it.start()
        }
    }

    fun hideTick() {

        tickAnimator?.cancel()

        if (tickAnimator == null) {

            initialiseTickAnimator()
        }

        tickAnimator?.let {

            it.startDelay = 0L
            it.setFloatValues(tickProgress, 0F)
            it.start()
        }
    }

    private fun initialiseTickAnimator() {

        tickAnimator = ValueAnimator().apply {

            duration = tickAnimationDuration
            interpolator = tickInterpolator

            addUpdateListener {

                tickProgress = it.animatedValue as Float
                invalidate()
            }
        }
    }

    private fun invalidateTextColor() {

        textView.setTextColor(ColorUtils.blendARGB(textColor, strikeColor, strikeProgress))
    }

    private fun invalidateDashColor() {

        linePaint.color = ColorUtils.blendARGB(dashColor, strikeColor, strikeProgress)
    }

    private fun animateStrikeTo(progress: Float) {

        strikeAnimator?.cancel()

        if (strikeAnimator == null) {

            strikeAnimator = ValueAnimator().apply {

                interpolator = FastOutSlowInInterpolator()

                addUpdateListener {

                    setStrikeAnimationProgress(it.animatedValue as Float)
                }
            }
        }

        strikeAnimator?.let {

            it.duration =
                (baseStrikeAnimationDuration * textView.lineCount.toFloat().pow(0.7F)).toLong()
            it.setFloatValues(strikeProgress, progress)
            it.start()
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        canvas?.let { c ->

            if (lineBounds.dirty) lineBounds.resolve(textView)

            var lineWidthToDraw = 0F

            for (i in 0 until textView.lineCount) {

                lineWidthToDraw += textView.layout.getLineWidth(i)
            }

            lineWidthToDraw *= strikeProgress

            // TODO Add extra strike on the right (strike padding?), looks a bit too close to the end of the text atm

            // Draw the dash
            val dashCy = lineBounds.boundsFor(0).exactCenterY()
            val firstLineWidth = textView.layout.getLineWidth(0)
            val firstLineProgress = (lineWidthToDraw / firstLineWidth).coerceAtMost(1F)
            val dashRadius = dashSize / 2
            val dashLineStart =
                lerp(checkAreaSize / 2 - dashRadius, checkAreaSize + textPadding, firstLineProgress)
            val dashLineEnd =
                lerp(
                    checkAreaSize / 2 + dashRadius,
                    checkAreaSize + textPadding + firstLineWidth,
                    firstLineProgress
                )
            c.drawLine(
                dashLineStart + textOffset,
                dashCy,
                dashLineEnd + textOffset,
                dashCy,
                linePaint
            )

            lineWidthToDraw -= firstLineWidth

            // Draw rest if needed
            for (i in 1 until textView.lineCount) {

                if (lineWidthToDraw <= 0) break

                val bounds = lineBounds.boundsFor(i)
                val startX = textOffset + bounds.left.toFloat()
                val width = textView.layout.getLineWidth(i)
                val lineCy = bounds.exactCenterY()

                c.drawLine(
                    startX,
                    lineCy,
                    startX + min(width, lineWidthToDraw),
                    lineCy,
                    linePaint
                )

                lineWidthToDraw -= width
            }

            if (tickProgress != 0F) {

                val animatedTickRadius = (tickSize / 2) * tickProgress
                val tickCx = checkAreaSize / 2
                tickDrawable.setBounds(
                    (tickCx - animatedTickRadius).roundToInt(),
                    (dashCy - animatedTickRadius).roundToInt(),
                    (tickCx + animatedTickRadius).roundToInt(),
                    (dashCy + animatedTickRadius).roundToInt()
                )
                tickDrawable.draw(c)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val w = MeasureSpec.getSize(widthMeasureSpec)
        val textViewMaxWidth = (w - checkAreaSize - textPadding).toInt()
        measureChild(
            textView,
            MeasureSpec.makeMeasureSpec(textViewMaxWidth, MeasureSpec.AT_MOST),
            heightMeasureSpec
        )

        // TODO Account for situation where tick is bigger than text, extra height it needed. Use first lines bounds
        val height =
            max(textView.measuredHeight, ceil(maxTickSize).toInt()) + paddingTop + paddingBottom
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return when (event?.action) {

            MotionEvent.ACTION_DOWN -> {

                parent.requestDisallowInterceptTouchEvent(true)

                touchedCheckArea = inCheckArea(event.x, event.y)
                if (!touchedCheckArea) dragStartX = event.x
                true
            }

            MotionEvent.ACTION_MOVE -> {

                if (!touchedCheckArea && canDrag()) {

                    val dX = max(0F, event.x - dragStartX)
                    val checkedAnimationProgress = min(dX / checkDragThresholdX, 1F)
                    val translateProgress = dragTranslationInterpolator.getInterpolation(dX / width)

                    setTextOffset(translateProgress * maxDragTranslateX)
                    setStrikeAnimationProgress(checkedAnimationProgress)
                    true
                } else {

                    super.onTouchEvent(event)
                }
            }

            MotionEvent.ACTION_UP -> {

                if (touchedCheckArea) {

                    if (inCheckArea(event.x, event.y)) {

                        performClick()
                        true
                    } else {

                        super.onTouchEvent(event)
                    }
                } else if (canDrag()) {

                    val dX = event.x - dragStartX

                    if (dX > 0) {

                        dragSettling = true

                        if (dX > checkDragThresholdX) registerChecked(true)

                        val oldTextOffset = textOffset
                        val oldStrikeProgress = strikeProgress

                        ValueAnimator.ofFloat(1F, 0F).run {

                            duration =
                                ((textOffset / maxDragTranslateX) * baseDragSettleAnimationDuration).toLong()
                            interpolator = dragTranslationInterpolator

                            addUpdateListener {

                                val value = it.animatedValue as Float

                                setTextOffset(lerp(0F, oldTextOffset, value))
                                if (!checked) setStrikeAnimationProgress(
                                    lerp(0F, oldStrikeProgress, value)
                                )
                            }

                            doOnEnd {

                                if (checked) showTick()
                                dragSettling = false
                            }

                            start()
                        }
                    }

                    true
                } else {

                    super.onTouchEvent(event)
                }
            }

            else -> super.onTouchEvent(event)

        }
    }

    override fun performClick(): Boolean {

        super.performClick()
        animateChecked(!checked)

        return true
    }

    private fun canDrag() = !checked && !dragSettling

    private fun inCheckArea(x: Float, y: Float): Boolean {

        return x <= checkAreaSize
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        val textLeft = ceil(checkAreaSize + textPadding).toInt()
        val textCy = (b - t).toFloat() / 2
        val textRise = textView.measuredHeight.toFloat() / 2
        val textTop = (textCy - textRise).toInt()
        val textBottom = ceil(textCy + textRise).toInt()

        textView.layout(textLeft, textTop, textLeft + textView.measuredWidth, textBottom)
    }

    class LineBounds {

        var dirty = true
        private var bounds: ArrayList<Rect>? = null

        fun resolve(textView: TextView) {

            ensureLineCount(textView.lineCount)
            for (i in 0 until textView.lineCount) {

                textView.getLineBounds(i, boundsFor(i))
                boundsFor(i).offset(textView.left, textView.top)
            }
        }

        fun boundsFor(line: Int): Rect {

            return bounds!![line]
        }

        private fun ensureLineCount(count: Int) {

            if (bounds == null) {

                bounds = ArrayList(count)
                repeat(count) {

                    bounds!!.add(Rect())
                }
            } else if (count > bounds!!.size) {

                bounds!!.ensureCapacity(count)
                repeat(count - bounds!!.size) {

                    bounds!!.add(Rect())
                }
            }
        }
    }

    interface OnCheckedChangeListener {

        fun onCheckChanged(checked: Boolean)
    }
}