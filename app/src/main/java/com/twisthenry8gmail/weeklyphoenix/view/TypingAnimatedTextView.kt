package com.twisthenry8gmail.weeklyphoenix.view

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TypingAnimatedTextView(context: Context, attrs: AttributeSet) :
    AppCompatTextView(context, attrs) {

    private val characterAnimationDuration = 40L

    private var eventualText: CharSequence = ""
    private var remainingToAnimate = 0

    private val animationHandler: Handler = Handler()

    private val animationRunnable: Runnable = Runnable {

        text = if(remainingToAnimate <= eventualText.length) {

            eventualText.subSequence(0, eventualText.length - remainingToAnimate + 1)
        } else {

            text.subSequence(0, text.length - 1)
        }

        remainingToAnimate--
        if(remainingToAnimate > 0) postAnimation()
    }

    fun animateText(charSequence: CharSequence) {

        if(charSequence == eventualText) return

        animationHandler.removeCallbacks(animationRunnable)

        eventualText = charSequence
        remainingToAnimate = charSequence.length + text.length
        animationRunnable.run()
    }

    private fun postAnimation() {

        animationHandler.postDelayed(animationRunnable, characterAnimationDuration)
    }
}