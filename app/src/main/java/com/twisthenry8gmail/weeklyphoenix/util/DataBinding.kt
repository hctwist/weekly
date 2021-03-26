package com.twisthenry8gmail.weeklyphoenix.util

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.databinding.*
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.twisthenry8gmail.dragline.DraglineView
import com.twisthenry8gmail.progresscircles.ProgressView
import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalHistory
import com.twisthenry8gmail.weeklyphoenix.view.goals.GoalProgressView

@InverseBindingMethods(value = [InverseBindingMethod(type = DraglineView::class, attribute = "value", method = "getValue")])
object DataBinding {

    fun and(vararg booleans: Boolean) = booleans.all { it }

    fun isNullOrEmpty(list: List<*>?): Boolean {

        return list.isNullOrEmpty()
    }

    @BindingAdapter("goneUnless")
    @JvmStatic
    fun goneUnless(view: View, bool: Boolean) {

        view.visibility = if (bool) View.VISIBLE else View.GONE
    }

    @BindingAdapter("invisibleUnless")
    @JvmStatic
    fun invisibleUnless(view: View, bool: Boolean) {

        view.visibility = if (bool) View.VISIBLE else View.INVISIBLE
    }

    @BindingAdapter("textColorAttr")
    @JvmStatic
    fun setTextColorAttr(textView: TextView, colorAttr: Int) {

        val context = textView.context

        try {

            textView.setTextColor(context.getColor(colorAttr))
            return
        } catch (e: Resources.NotFoundException) {

            textView.setTextColor(ColorUtil.resolveColorAttribute(context, colorAttr))
        }
    }

    @BindingAdapter("valueAttrChanged")
    @JvmStatic
    fun setDraglineViewListener(draglineView: DraglineView, listener: InverseBindingListener) {

        draglineView.valueChangedListener = {

            listener.onChange()
        }
    }

    // TODO Improve this? Maybe the view needs to be refactored?
    @BindingAdapter("goalId", "goalProgress", "goalTarget")
    @JvmStatic
    fun bindGoalProgressView(
        goalProgressView: GoalProgressView,
        goalId: Int,
        goalProgress: Long,
        goalTarget: Long
    ) {

        if (!goalProgressView.hasBeenInitialised()) {

            goalProgressView.initialise(goalId, goalProgress, goalTarget)
        } else {

            goalProgressView.updateProgress(goalProgress, goalTarget, true)
        }
    }

    @BindingAdapter("extended")
    @JvmStatic
    fun extended(extendedFab: ExtendedFloatingActionButton, extended: Boolean) {

        // TODO Is there a better solution?
        // To make sure the motion is performed, the fab must have an icon
        if (extendedFab.icon == null) extendedFab.icon = ColorDrawable()

        if (extended) {

            extendedFab.extend()
        } else {

            extendedFab.shrink()
        }
    }
}