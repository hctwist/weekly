package com.twisthenry8gmail.weeklyphoenix.util

import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment

fun View.showSoftKeyboard() {

    requestFocus()
    context.getSystemService<InputMethodManager>()
        ?.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Fragment.hideSoftKeyboard() {

    activity?.currentFocus?.let {
        context?.getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(
            it.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }
}

fun EditText.replaceText(text: CharSequence) {

    setText(null)
    append(text)
}