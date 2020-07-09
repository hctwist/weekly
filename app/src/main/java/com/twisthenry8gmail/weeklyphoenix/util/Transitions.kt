package com.twisthenry8gmail.weeklyphoenix.util

import android.content.Context
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.Transition
import com.twisthenry8gmail.weeklyphoenix.R

object Transitions {

    const val CONTAINER_COLOR = "container_color"

    fun initialiseEnterTransition(context: Context, transition: Transition): Transition {

        transition.duration =
            context.resources.getInteger(R.integer.shared_enter_transition_duration).toLong()
        transition.interpolator = FastOutSlowInInterpolator()

        return transition
    }

    fun initialiseExitTransition(context: Context, transition: Transition): Transition {

        transition.duration =
            context.resources.getInteger(R.integer.shared_exit_transition_duration).toLong()
        transition.interpolator = FastOutSlowInInterpolator()

        return transition
    }


    object Names {

        const val VIEW_GOAL_TITLE = "title"
        const val VIEW_GOAL_PROGRESS = "progress"

        const val VIEW_OVERDUE_TASKS_CONTAINER = "overdue_container"

        fun goalCard(goalId: Int) = "goal_card$goalId"

        fun taskCard(taskSnapshotDate: Long) = "task_card$taskSnapshotDate"
    }
}