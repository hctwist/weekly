package com.twisthenry8gmail.weeklyphoenix.util

import android.content.Context
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.Transition
import com.twisthenry8gmail.weeklyphoenix.R

object Transitions {

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

    object ViewGoal {

        const val TRANSITION_NAME_TITLE = "title"
        const val TRANSITION_NAME_PROGRESS = "progress"
    }
}