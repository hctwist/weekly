package com.twisthenry8gmail.weeklyphoenix.util

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.card.MaterialCardView
import com.twisthenry8gmail.dragline.DraglineView
import com.twisthenry8gmail.progresscircles.ProgressView
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalHistory
import com.twisthenry8gmail.weeklyphoenix.view.GoalProgressView
import com.twisthenry8gmail.weeklyphoenix.view.views.GoalProgressView2

object BindingAdapters {

    @BindingAdapter("goneUnless")
    @JvmStatic
    fun goneUnless(view: View, bool: Boolean) {

        view.visibility = if (bool) View.VISIBLE else View.GONE
    }

    @BindingAdapter("goal", "bindProgress", requireAll = false)
    @JvmStatic
    fun bindGoalToProgressView(
        progressView: ProgressView,
        goal: Goal?,
        bindProgress: Boolean?
    ) {

        goal?.let {

            progressView.run {

                target = it.target
                setColor(it.color)
                setBackingArcColor(ColorUtil.lightenGoalColor(it.color))
                if (bindProgress != false) setProgress(it.progress)
            }
        }
    }

    @BindingAdapter("goal", "goalHistory")
    @JvmStatic
    fun bindGoalToProgressView(
        progressView: ProgressView,
        goal: Goal,
        goalHistory: GoalHistory
    ) {

        progressView.run {

            target = goalHistory.target
            setColor(goal.color)
            setBackingArcColor(ColorUtil.lightenGoalColor(goal.color))
            setProgress(goalHistory.progress)
        }
    }

    interface DraglineTextFactory {

        fun generateTextFor(value: Long): String
    }

    @BindingAdapter("draglineTextFactory")
    @JvmStatic
    fun setDraglineTextFactory(draglineView: DraglineView, textFactory: DraglineTextFactory) {

        draglineView.textFactory = {

            textFactory.generateTextFor(it)
        }
    }

    // TODO Sort out, why do I need some of these?! - STACK
    @BindingAdapter("valueAttrChanged")
    @JvmStatic
    fun setDraglineListener(draglineView: DraglineView, listener: InverseBindingListener) {

        draglineView.valueChangedListener = {

            listener.onChange()
        }
    }

    @InverseBindingAdapter(attribute = "value")
    @JvmStatic
    fun getValueTest(draglineView: DraglineView): Long {

        return draglineView.value
    }

    @BindingAdapter("goal")
    @JvmStatic
    fun bindGoalToProgressView(goalProgressView: GoalProgressView, goal: Goal) {

        goalProgressView.initialise(goal)
    }

    @BindingAdapter("goal")
    @JvmStatic
    fun bindGoalToProgressView(goalProgressView: GoalProgressView2, goal: Goal) {

        goalProgressView.initialise(goal)
    }
//    @BindingAdapter("value")
//    @JvmStatic
//    fun setValueTest(draglineView: DraglineView, value: Long) {
//
//        draglineView.value = value
//    }

    @BindingAdapter("android:checked")
    @JvmStatic
    fun setChecked(materialCardView: MaterialCardView, checked: Boolean) {

        materialCardView.isChecked = checked
    }
}