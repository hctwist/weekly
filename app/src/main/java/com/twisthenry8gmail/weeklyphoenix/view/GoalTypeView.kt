package com.twisthenry8gmail.weeklyphoenix.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.card.MaterialCardView
import com.twisthenry8gmail.weeklyphoenix.R
import kotlinx.android.synthetic.main.goal_type_view.view.*

class GoalTypeView(context: Context, attrs: AttributeSet) :
    MaterialCardView(ContextThemeWrapper(context, R.style.GoalTypeView), attrs) {

    init {

        View.inflate(context, R.layout.goal_type_view, this)

        context.obtainStyledAttributes(attrs, R.styleable.GoalTypeView).run {

            val title = getString(R.styleable.GoalTypeView_goalTypeTitle)!!
            val icon = getDrawable(R.styleable.GoalTypeView_goalTypeIcon)!!
            val description = getString(R.styleable.GoalTypeView_goalTypeDescription)!!

            goal_type_title.text = title
            goal_type_icon.setImageDrawable(icon)
            goal_type_description.text = description

            recycle()
        }
    }
}