package com.twisthenry8gmail.weeklyphoenix.util

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.twisthenry8gmail.dragline.DraglineView
import com.twisthenry8gmail.progresscircles.ProgressView
import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalHistory
import com.twisthenry8gmail.weeklyphoenix.view.goals.GoalProgressView

object DataBinding {

    fun and(b1: Boolean, b2: Boolean) = b1 && b2

    // TODO Mess

    // TODO Better solution?
    @BindingAdapter("textColorAttr")
    @JvmStatic
    fun setTextColorAttr(textView: TextView, colorAttr: Int) {

        val context = textView.context

        try {

            textView.setTextColor(context.getColor(colorAttr))
            return
        } catch (e: Resources.NotFoundException) {

            val typedValue = TypedValue()
            context.theme.resolveAttribute(colorAttr, typedValue, true)

            val colorRes =
                if (typedValue.resourceId == 0) typedValue.data else typedValue.resourceId
            textView.setTextColor(context.getColor(colorRes))
        }
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

    @BindingAdapter("progress")
    @JvmStatic
    fun setProgress(progressView: ProgressView, progress: Long) {

        progressView.setProgress(progress, false)
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
                setBackingColor(ColorUtil.lightenGoalColor(it.color))
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
            setBackingColor(ColorUtil.lightenGoalColor(goal.color))
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

    // TODO Rename this goal progress view? Graph view?
    @BindingAdapter("goal", "useLightGoalColor", requireAll = false)
    @JvmStatic
    fun bindGoalToProgressView(
        goalProgressView: GoalProgressView,
        goal: Goal?,
        useLightGoalColor: Boolean?
    ) {

        goal?.let { g ->

            if (!goalProgressView.hasBeenInitialised()) {

                if (useLightGoalColor == null) {
                    goalProgressView.initialise(g)
                } else {
                    goalProgressView.initialise(g, useLightGoalColor)
                }
            } else {

                goalProgressView.updateProgress(g.progress, g.target, true)
            }
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