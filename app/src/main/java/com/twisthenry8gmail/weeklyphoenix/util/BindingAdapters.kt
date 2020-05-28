package com.twisthenry8gmail.weeklyphoenix.util

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.twisthenry8gmail.dragline.DraglineView
import com.twisthenry8gmail.progresscircles.ProgressCircleView
import com.twisthenry8gmail.weeklyphoenix.data.Goal

object BindingAdapters {

    @BindingAdapter("goneUnless")
    @JvmStatic
    fun goneUnless(view: View, bool: Boolean) {

        view.visibility = if (bool) View.VISIBLE else View.GONE
    }

    @BindingAdapter("goal")
    @JvmStatic
    fun bindGoalToProgressView(progressView: ProgressCircleView, goal: Goal) {

        progressView.run {

            target = goal.target
            setColor(goal.color)
            setBackingArcColor(ColorUtil.lightenGoalColor(goal.color))
            setProgress(goal.progress)
        }
    }

    interface DraglineTextFactory {

        fun generateTextFor(value: Long): String
    }

    @BindingAdapter("draglineTextFactory")
    @JvmStatic
    fun onDraglineValueChanged(draglineView: DraglineView, textFactory: DraglineTextFactory) {

        draglineView.textFactory = {

            textFactory.generateTextFor(it)
        }
    }

    // TODO Sort out, why do I need some of these?!
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

//    @BindingAdapter("value")
//    @JvmStatic
//    fun setValueTest(draglineView: DraglineView, value: Long) {
//
//        draglineView.value = value
//    }
}