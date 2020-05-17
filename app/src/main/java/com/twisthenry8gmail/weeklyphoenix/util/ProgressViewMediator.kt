package com.twisthenry8gmail.weeklyphoenix.util

import androidx.databinding.BindingAdapter
import com.twisthenry8gmail.progresscircles.ProgressCircleView
import com.twisthenry8gmail.weeklyphoenix.data.Goal

object ProgressViewMediator {

    @BindingAdapter("goal")
    @JvmStatic
    fun bind(progressView: ProgressCircleView, goal: Goal) {

        progressView.run {

            target = goal.target
            setColor(goal.color)
            setProgress(goal.progress)
        }
    }
}