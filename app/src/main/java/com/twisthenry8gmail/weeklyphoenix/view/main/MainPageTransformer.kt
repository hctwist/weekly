package com.twisthenry8gmail.weeklyphoenix.view.main

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.viewpager2.widget.ViewPager2
import com.twisthenry8gmail.weeklyphoenix.R
import kotlinx.android.synthetic.main.goal_card_2.view.*
import kotlin.math.abs

class MainPageTransformer(context: Context) :
    ViewPager2.PageTransformer {

    private val peekOffset = context.resources.getDimension(R.dimen.margin)
    private val foregroundColor = context.getColor(R.color.light_grey)
    private val rtlMultiplier =
        if (context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) 1 else -1
    private var pageMarginCache = 0

    override fun transformPage(page: View, position: Float) {

        if (position == 0F) {

            // Only fetch the margin once per scroll
            pageMarginCache = (page.layoutParams as ViewGroup.MarginLayoutParams).marginStart
        }

        val colorOffset = abs(position.coerceIn(-1F, 1F))
        val color = if (colorOffset > COLOR_CHANGE_THRESHOLD) {

            val alpha = (colorOffset - COLOR_CHANGE_THRESHOLD) / (1 - COLOR_CHANGE_THRESHOLD)
            ColorUtils.setAlphaComponent(
                foregroundColor,
                (alpha * 255).toInt()
            )
        } else {

            Color.TRANSPARENT
        }
        page.goal_card_root.setBackgroundColor(color)

        page.translationX = position * rtlMultiplier * (pageMarginCache + peekOffset)
    }

    companion object {

        const val COLOR_CHANGE_THRESHOLD = 0.65F
    }
}