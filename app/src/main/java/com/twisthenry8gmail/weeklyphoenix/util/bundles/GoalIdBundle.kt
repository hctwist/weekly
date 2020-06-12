package com.twisthenry8gmail.weeklyphoenix.util.bundles

import android.os.Bundle

object GoalIdBundle {

    private const val GOAL_ID = "goal_id"

    operator fun invoke(id: Int): Bundle {

        return Bundle().apply {

            putInt(GOAL_ID, id)
        }
    }

    fun extractId(bundle: Bundle?): Int {

        if (bundle?.containsKey(GOAL_ID) != true) throw IllegalArgumentException("This must have a goal id passed through it's argument")
        return bundle.getInt(GOAL_ID)
    }
}