package com.twisthenry8gmail.weeklyphoenix.view.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.google.android.material.card.MaterialCardView
import com.twisthenry8gmail.weeklyphoenix.R
import kotlinx.android.synthetic.main.goal_type_view.view.*

class GoalTypeView(context: Context, attrs: AttributeSet) :
    MaterialCardView(context, attrs, R.attr.goalTypeStyle) {

    val textColor: ColorStateList?
    val iconTint: ColorStateList?

    init {

        View.inflate(context, R.layout.goal_type_view, this)

        context.obtainStyledAttributes(attrs, R.styleable.GoalTypeView, R.attr.goalTypeStyle, 0)
            .run {

                val title = getString(R.styleable.GoalTypeView_goalTypeTitle)!!
                val icon = getDrawable(R.styleable.GoalTypeView_goalTypeIcon)!!

                // RELEASE Remove
                val description = getString(R.styleable.GoalTypeView_goalTypeDescription)!!

                goal_type_title.text = title
                goal_type_icon.setImageDrawable(icon)

                textColor = getColorStateList(R.styleable.GoalTypeView_goalTypeTextColor)
                iconTint = getColorStateList(R.styleable.GoalTypeView_goalTypeIconTint)

                recycle()
            }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()

        textColor?.getColorForState(drawableState, Color.BLACK)?.let {

            goal_type_title.setTextColor(it)
        }

        iconTint?.getColorForState(drawableState, Color.BLACK)?.let {

            goal_type_icon.imageTintList = ColorStateList.valueOf(it)
        }

    }
}