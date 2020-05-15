package com.twisthenry8gmail.weeklyphoenix.view.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.recyclerextensions.StateRecyclerAdapter
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.util.GoalSubtitleGenerator
import com.twisthenry8gmail.weeklyphoenix.view.GoalProgressView
import kotlinx.android.synthetic.main.goal_card.view.*
import kotlinx.android.synthetic.main.goal_card_complete.view.*
import kotlinx.android.synthetic.main.main_header.view.*

// TODO
class StatefulGoalAdapter(val context: Context) :
    StateRecyclerAdapter(R.layout.goals_empty) {

    companion object {

        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_GOAL = 1
        const val VIEW_TYPE_GOAL_COMPLETE = 2
    }

    var heading = ""
    var subheading = ""
    var goals = listOf<Goal>()
        set(value) {

            field = value
            notifyLoadingFinished()
        }
    private val goalSubtitleGenerator =
        GoalSubtitleGenerator(context)

    var actionListener: ((Goal, Int, View) -> Unit)? = null

    override fun getEmptyItemCount() = 1

    override fun getStatefulItemCount(): Int {

        return goals.size + 1
    }

    override fun getStatefulItemViewType(position: Int, isLoading: Boolean): Int {

        return if (position == 0) {

            VIEW_TYPE_HEADER
        } else {

            if (isLoading || !goals[position - 1].isComplete()) {

                VIEW_TYPE_GOAL
            } else {

                VIEW_TYPE_GOAL_COMPLETE
            }
        }
    }

    override fun onCreateStatefulViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return when (viewType) {

            VIEW_TYPE_HEADER -> HeaderVH(
                LayoutInflater.from(context).inflate(
                    R.layout.main_header,
                    parent,
                    false
                )
            )
            VIEW_TYPE_GOAL -> GoalVH(
                LayoutInflater.from(context).inflate(
                    R.layout.goal_card,
                    parent,
                    false
                )
            )
            else -> GoalCompleteVH(
                LayoutInflater.from(context)
                    .inflate(
                        R.layout.goal_card_complete,
                        parent,
                        false
                    )
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {

        if (payloads.isEmpty()) {

            super.onBindViewHolder(holder, position, payloads)
        } else {

            payloads.filterIsInstance<Change>().forEach {

                when (it) {

                    Change.PROGRESS -> {

                        val h = holder as GoalVH
                        val goal = goals[position - 1]

                        h.progressView.setProgress(goal.progress, true)
                        h.subtitleView.text = goalSubtitleGenerator.generateSubtitle(goal)
                    }
                }
            }
        }
    }

    override fun onBindStatefulViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        isLoading: Boolean
    ) {

        when (getItemViewType(position)) {

            VIEW_TYPE_HEADER -> {

                val headerVH = holder as HeaderVH

                headerVH.headingView.text = heading
                headerVH.subheadingView.text = subheading
            }

            VIEW_TYPE_GOAL -> {

                val goalVH = holder as GoalVH

                val toggleVisibilityList = listOf(goalVH.titleView, goalVH.subtitleView, goalVH.actionButton)

                if (isLoading) {

                    toggleVisibilityList.forEach {

                        it.visibility = View.INVISIBLE
                    }
                } else {

                    toggleVisibilityList.forEach {

                        it.visibility = View.VISIBLE
                    }

                    val goal = goals[position - 1]

                    goalVH.titleView.text = goal.name
                    goalVH.subtitleView.text = goalSubtitleGenerator.generateSubtitle(goal)

                    goalVH.progressView.apply {

                        setColor(goal.color)
                        maxProgress = goal.target
                        setProgress(goal.progress)
                    }

                    goalVH.actionButton.setImageResource(

                        when (goal.type) {

                            Goal.Type.COUNTED -> R.drawable.round_plus_one_24
                            Goal.Type.TIMED -> R.drawable.round_play_arrow_24
                        }
                    )
                    goalVH.actionButton.setOnClickListener {

                        actionListener?.invoke(goal, position, goalVH.itemView)
                    }
                }
            }

            VIEW_TYPE_GOAL_COMPLETE -> {

                val goalCVH = holder as GoalCompleteVH

                val goal = goals[position - 1]

                goalCVH.titleView.text = goal.name
            }
        }
    }

    class HeaderVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val headingView: TextView = itemView.main_header_heading
        val subheadingView: TextView = itemView.main_header_subheading
    }

    class GoalVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleView: TextView = itemView.goal_title
        val subtitleView: TextView = itemView.goal_subtitle
        val progressView: GoalProgressView = itemView.goal_progress_view
        val actionButton: ImageView = itemView.goal_action_button
    }

    class GoalCompleteVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleView: TextView = itemView.goal_complete_title
    }

    enum class Change {

        PROGRESS
    }
}