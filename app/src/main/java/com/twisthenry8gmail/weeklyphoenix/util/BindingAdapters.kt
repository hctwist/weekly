package com.twisthenry8gmail.weeklyphoenix.util

import android.content.res.Resources
import android.icu.text.MessageFormat
import android.icu.text.PluralRules
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.card.MaterialCardView
import com.twisthenry8gmail.dragline.DraglineView
import com.twisthenry8gmail.progresscircles.ProgressView
import com.twisthenry8gmail.weeklyphoenix.ButtonSwitcher
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalHistory
import com.twisthenry8gmail.weeklyphoenix.view.GoalProgressView
import com.twisthenry8gmail.weeklyphoenix.view.TypingAnimatedTextView
import com.twisthenry8gmail.weeklyphoenix.view.views.GoalProgressView2
import java.util.*

object BindingAdapters {

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

    @BindingAdapter("backingColor")
    @JvmStatic
    fun setBackingColor(progressView: ProgressView, color: Int) {

        progressView.setBackingColor(color)
    }

    @BindingAdapter("color")
    @JvmStatic
    fun setColor(progressView: ProgressView, color: Int) {

        progressView.setColor(color)
    }

    @BindingAdapter("progress")
    @JvmStatic
    fun setProgress(progressView: ProgressView, progress: Long) {

        progressView.setProgress(progress, true)
    }

    @BindingAdapter("bind:target")
    @JvmStatic
    fun setTarget(progressView: ProgressView, target: Long) {

        progressView.target = target
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

    @BindingAdapter("goal")
    @JvmStatic
    fun bindGoalToProgressView(goalProgressView: GoalProgressView, goal: Goal) {

        goalProgressView.initialise(goal)
    }

    // TODO Rename this goal progress view? Graph view?
    @BindingAdapter("goal", "useLightGoalColor", requireAll = false)
    @JvmStatic
    fun bindGoalToProgressView(
        goalProgressView: GoalProgressView2,
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