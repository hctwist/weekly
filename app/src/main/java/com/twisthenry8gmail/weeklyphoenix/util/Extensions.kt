package com.twisthenry8gmail.weeklyphoenix.util

import android.graphics.PointF
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.math.MathUtils

fun View.showSoftKeyboard() {

    requestFocus()
    context.getSystemService<InputMethodManager>()
        ?.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Fragment.hideSoftKeyboard() {

    val windowToken = activity?.currentFocus?.windowToken
    context?.getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(
        windowToken, InputMethodManager.HIDE_NOT_ALWAYS
    )
}

fun EditText.replaceText(text: CharSequence) {

    setText(null)
    append(text)
}

fun PointF.setAsLerpBetween(p1: PointF, p2: PointF, fraction: Float) {

    x = MathUtils.lerp(p1.x, p2.x, fraction)
    y = MathUtils.lerp(p1.y, p2.y, fraction)
}