package com.twisthenry8gmail.weeklyphoenix.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.twisthenry8gmail.recyclerextensions.StatefulRecyclerHelper
import com.twisthenry8gmail.weeklyphoenix.databinding.GoalCardBinding

class GoalLoadingAdapter : StatefulRecyclerHelper.LoadingAdapter<GoalViewHolder.Content>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder.Content {

        return GoalViewHolder.Content(
            GoalCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GoalViewHolder.Content, position: Int) {

        holder.bindLoading()
    }
}