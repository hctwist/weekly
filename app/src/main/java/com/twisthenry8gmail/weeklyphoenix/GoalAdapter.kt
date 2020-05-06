package com.twisthenry8gmail.weeklyphoenix

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.goal_card.view.*
import kotlinx.android.synthetic.main.main_header.view.*

class GoalAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_CONTENT = 1
    }

    var heading = ""
    var subheading = ""
    var goals = listOf<Goal>()
        set(value) {

            field = value
            notifyDataSetChanged()
        }

    var actionListener: ((Goal, Int) -> Unit)? = null

    override fun getItemCount() = goals.size + 1

    override fun getItemViewType(position: Int) =
        if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_CONTENT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_HEADER) {

            HeaderVH(
                LayoutInflater.from(parent.context).inflate(R.layout.main_header, parent, false)
            )
        } else {

            VH(
                LayoutInflater.from(parent.context).inflate(R.layout.goal_card, parent, false)
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

                        (holder as VH).progressView.setProgress(goals[position - 1].current, true)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(getItemViewType(position) == VIEW_TYPE_HEADER) {

            val headerVH = holder as HeaderVH

            headerVH.headingView.text = heading
            headerVH.subheadingView.text = subheading
        }
        else {

            val vh = holder as VH

            val goal = goals[position - 1]

            vh.titleView.text = goal.name
            vh.subtitleView.text = "Subtitle"

            vh.progressView.apply {

                setColor(goal.color)
                maxProgress = goal.target
                setProgress(goal.current)
            }

            vh.actionButton.setImageResource(

                when (goal.type) {

                    Goal.Type.COUNTED -> R.drawable.round_plus_one_24
                    Goal.Type.TIMED -> R.drawable.round_play_arrow_24
                }
            )
            vh.actionButton.setOnClickListener {

                actionListener?.invoke(goal, position)
            }
        }
    }

    class HeaderVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val headingView: TextView = itemView.main_header_heading
        val subheadingView: TextView = itemView.main_header_subheading
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleView: TextView = itemView.goal_title
        val subtitleView: TextView = itemView.goal_subtitle
        val progressView: GoalProgressView = itemView.goal_progress_view
        val actionButton: ImageView = itemView.goal_action_button
    }

    enum class Change {

        PROGRESS
    }
}