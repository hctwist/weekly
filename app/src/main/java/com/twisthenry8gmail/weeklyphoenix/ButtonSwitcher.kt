package com.twisthenry8gmail.weeklyphoenix

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import androidx.core.animation.doOnEnd
import androidx.core.graphics.ColorUtils
import androidx.databinding.BindingAdapter
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.button.MaterialButton
import kotlin.math.roundToInt

// TODO Is there not a built in option?
class ButtonSwitcher(context: Context, attrs: AttributeSet) : MaterialButton(context, attrs) {

    private var wms: Int? = null
    private var hms: Int? = null

    // TODO Background color too dark fromm defaultState?
    init {

        setLines(1)
        ellipsize = TextUtils.TruncateAt.END
    }

    fun switch(buttonText: String, backgroundColor: Int) {

        val oldWidth = measuredWidth
        val oldText = text
        text = buttonText

        if (wms != null) {

            measure(wms!!, hms!!)
            val width = measuredWidth
            text = oldText

            val oldBackground = backgroundTintList?.defaultColor

            val oldTextColor = textColors.getColorForState(drawableState, 0)

            val fadeInAnimator = ValueAnimator.ofFloat(0F, 1F).apply {

                interpolator = FastOutSlowInInterpolator()
                duration = 150

                addUpdateListener {

                    addUpdateListener {

                        val color = ColorUtils.blendARGB(
                            Color.TRANSPARENT,
                            oldTextColor,
                            it.animatedValue as Float
                        )

                        setTextColor(color)
                    }
                }
            }

            val switchAnimator = ValueAnimator.ofFloat(0F, 1F).apply {

                interpolator = FastOutSlowInInterpolator()
                duration = 250

                addUpdateListener { animator ->

                    val value = animator.animatedValue as Float

                    layoutParams.width = (oldWidth + (width - oldWidth) * value).roundToInt()

                    oldBackground?.let {

                        val c = ColorUtils.blendARGB(it, backgroundColor, value)
                        setBackgroundColor(c)
                    }

                    if (value > 0.5) {

                        text = buttonText
                    }

                    requestLayout()
                }

                doOnEnd {

                    fadeInAnimator.start()
                }
            }

            val fadeOutAnimator = ValueAnimator.ofFloat(0F, 1F).apply {

                interpolator = FastOutSlowInInterpolator()
                duration = 150

                addUpdateListener {

                    val color = ColorUtils.blendARGB(
                        oldTextColor,
                        Color.TRANSPARENT,
                        it.animatedValue as Float
                    )

                    setTextColor(color)
                }

                doOnEnd {

                    switchAnimator.start()
                }

                start()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        wms = widthMeasureSpec
        hms = heightMeasureSpec

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    companion object {

        @BindingAdapter("animatedText", "animatedBackgroundColor")
        @JvmStatic
        fun switchTextAndBackground(
            buttonSwitcher: ButtonSwitcher,
            text: String,
            backgroundColor: Int
        ) {

            buttonSwitcher.switch(text, backgroundColor)
        }
    }
}