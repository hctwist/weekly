package com.twisthenry8gmail.weeklyphoenix.view.addgoal

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.card.MaterialCardView
import com.twisthenry8gmail.weeklyphoenix.R
import kotlinx.android.synthetic.main.goal_type_view.view.*

class GoalTypeView(context: Context, attrs: AttributeSet) :
    MaterialCardView(context, attrs, R.attr.goalTypeStyle) {

    init {

        View.inflate(context, R.layout.goal_type_view, this)

        context.obtainStyledAttributes(attrs, R.styleable.GoalTypeView, R.attr.goalTypeStyle, 0)
            .run {

                val title = getString(R.styleable.GoalTypeView_goalTypeTitle)!!
                val icon = getDrawable(R.styleable.GoalTypeView_goalTypeIcon)!!

                goal_type_title.text = title
                goal_type_icon.setImageDrawable(icon)
                recycle()
            }
    }
}