package com.twisthenry8gmail.weeklyphoenix.view.main

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatImageButton
import com.twisthenry8gmail.buttons.AccessibleButtonsUtil
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.databinding.LayoutPopupViewBinding


class LayoutPopupView(context: Context, attrs: AttributeSet) : AppCompatImageButton(context, attrs),
    View.OnClickListener {

    var layoutSelectedListener: LayoutSelectedListener? = null

    var layoutSelected = MainLayout.DEFAULT
        set(value) {

            field = value
            setImageResource(value.iconRes)
        }

    private var popupWindow: PopupWindow? = null

    init {

        val outValue = TypedValue()
        context.theme.resolveAttribute(
            android.R.attr.selectableItemBackgroundBorderless,
            outValue,
            true
        )
        setBackgroundResource(outValue.resourceId)

        setOnClickListener(this)

        AccessibleButtonsUtil.expandTouchRegion(
            this,
            resources.getDimensionPixelSize(R.dimen.accessibility_padding)
        )
    }

    override fun onClick(v: View?) {

        val layoutInflater = LayoutInflater.from(context)
        val binding = LayoutPopupViewBinding.inflate(layoutInflater)
        binding.listener = object : LayoutSelectedListener {

            override fun onLayoutSelected(layout: MainLayout) {

                onLayoutSelectedInternal(layout)
            }
        }
        binding.layoutselected = layoutSelected

        popupWindow = PopupWindow(
            binding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).also {

            it.isOutsideTouchable = true
            it.elevation = resources.getDimension(R.dimen.margin)
            it.showAsDropDown(this@LayoutPopupView, 0, 0, Gravity.END)
        }
    }

    private fun onLayoutSelectedInternal(layout: MainLayout) {

        popupWindow?.dismiss()

        if (layout != layoutSelected) {

            layoutSelected = layout
            layoutSelectedListener?.onLayoutSelected(layout)
        }
    }

    interface LayoutSelectedListener {

        fun onLayoutSelected(layout: MainLayout)
    }
}