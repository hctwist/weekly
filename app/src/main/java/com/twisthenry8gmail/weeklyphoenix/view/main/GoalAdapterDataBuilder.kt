package com.twisthenry8gmail.weeklyphoenix.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.twisthenry8gmail.weeklyphoenix.data.Goal

object GoalAdapterDataBuilder {

    fun build(header: GoalAdapter.Header, goals: List<Goal>): List<GoalAdapter.Data> {

        val data = ArrayList<GoalAdapter.Data>(1 + goals.size)
        data.add(GoalAdapter.Data.ofHeader(header))

        goals.forEach {

            data.add(GoalAdapter.Data.ofGoal(it))
        }

        return data
    }
}