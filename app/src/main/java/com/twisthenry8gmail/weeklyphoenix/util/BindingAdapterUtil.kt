package com.twisthenry8gmail.weeklyphoenix.util

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("goneUnless")
fun goneUnless(view: View, bool: Boolean) {

    view.visibility = if (bool) View.VISIBLE else View.GONE
}