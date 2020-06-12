package com.twisthenry8gmail.weeklyphoenix.view.main

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView

class GoalCardView(context: Context, attrs: AttributeSet): MaterialCardView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec)
    }
}