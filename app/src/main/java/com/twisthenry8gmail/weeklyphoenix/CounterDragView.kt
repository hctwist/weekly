package com.twisthenry8gmail.weeklyphoenix

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.counter_drag_view.view.*
import kotlin.math.roundToInt

class CounterDragView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    companion object {

        const val WIDEST_MOVE_INCREMENT = 10
    }

    var value = 0

    var valueChangedListener: (Int) -> Unit = {}

    private var actionStartX = 0F
    private var actionStartValue = 0
    private var actionValue = 0

    init {

        inflate(context, R.layout.counter_drag_view, this)

        counter_drag_switcher.setFactory {

            TextView(context).apply {

                inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

        counter_drag_switcher.inAnimation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        counter_drag_switcher.outAnimation = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right)

        counter_drag_switcher.setCurrentText(value.toString())

        setOnTouchListener { _, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {

                    actionStartX = event.x
                    actionStartValue = value
                }

                MotionEvent.ACTION_MOVE -> {

                    val dx = event.x - actionStartX
                    val increment = ((dx / width) * WIDEST_MOVE_INCREMENT).roundToInt()

                    val newActionValue = actionStartValue + increment
                    if (newActionValue != actionValue) {
                        actionValue = newActionValue
                        counter_drag_switcher.setText(actionValue.toString())
                    }
                }

                MotionEvent.ACTION_UP -> {

                    value = actionValue
                    valueChangedListener(value)
                }
            }

            true
        }
    }
}