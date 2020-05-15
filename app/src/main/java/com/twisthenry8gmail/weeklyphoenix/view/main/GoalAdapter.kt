package com.twisthenry8gmail.weeklyphoenix.view.main

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.util.GoalSubtitleGenerator
import com.twisthenry8gmail.weeklyphoenix.view.GoalProgressView
import kotlinx.android.synthetic.main.goal_card.view.*
import kotlinx.android.synthetic.main.goal_card_complete.view.*
import kotlinx.android.synthetic.main.main_header.view.*
import java.lang.Exception

class GoalAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = listOf<Data>()

    private val goalSubtitleGenerator = GoalSubtitleGenerator(context)

    var goalClickListener: ((Goal) -> Unit)? = null
    var goalActionListener: ((Goal, Int, View) -> Unit)? = null

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int {

        return data[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (Data.Type.values()[viewType]) {

            Data.Type.HEADER -> HeaderVH(
                LayoutInflater.from(context).inflate(
                    R.layout.main_header,
                    parent,
                    false
                )
            )
            Data.Type.GOAL -> GoalVH(
                LayoutInflater.from(context).inflate(
                    R.layout.goal_card,
                    parent,
                    false
                )
            )
            Data.Type.GOAL_COMPLETE -> GoalCompleteVH(
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
                        val goal = data[position].asGoal()

                        h.progressView.setProgress(goal.progress, true)
                        h.subtitleView.text = goalSubtitleGenerator.generateSubtitle(goal)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (Data.Type.values()[getItemViewType(position)]) {

            Data.Type.HEADER -> {

                val headerVH = holder as HeaderVH
                val header = data[position].asHeader()

                headerVH.headingView.text = header.heading
                headerVH.subheadingView.text = header.subHeading
            }

            Data.Type.GOAL -> {

                val goalVH = holder as GoalVH
                val goal = data[position].asGoal()

                goalVH.itemView.setOnClickListener {

                    val adapterPosition = goalVH.adapterPosition
                    goalClickListener?.invoke(data[adapterPosition].asGoal())
                }

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

                    val adapterPosition = goalVH.adapterPosition
                    goalActionListener?.invoke(
                        data[adapterPosition].asGoal(),
                        adapterPosition,
                        goalVH.itemView
                    )
                }
            }

            Data.Type.GOAL_COMPLETE -> {

                val goalCVH = holder as GoalCompleteVH
                val goal = data[position].asGoal()

                goalCVH.itemView.setOnClickListener {

                    val adapterPosition = goalCVH.adapterPosition
                    goalClickListener?.invoke(data[adapterPosition].asGoal())
                }

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

    class ItemDecoration : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {

            val adapterPosition = parent.getChildAdapterPosition(view)

            if (adapterPosition == 0) {

                outRect.bottom = parent.resources.getDimensionPixelSize(R.dimen.double_margin)
            } else if (adapterPosition != state.itemCount - 1) {

                outRect.bottom = parent.resources.getDimensionPixelSize(R.dimen.margin)
            }
        }
    }

    class Data private constructor(val type: Type) {

        private var header: Header? = null
        private var goal: Goal? = null

        companion object {

            fun ofHeader(h: Header): Data {

                return Data(
                    Type.HEADER
                ).apply {

                    header = h
                }
            }

            fun ofGoal(g: Goal): Data {

                return Data(
                    if (g.isComplete()) Type.GOAL_COMPLETE else Type.GOAL
                ).apply {

                    goal = g
                }
            }
        }

        fun asHeader(): Header {

            return header ?: throw Exception("This data is not of type Header")
        }

        fun asGoal(): Goal {

            return goal ?: throw Exception("This data is not of type Goal")
        }

        enum class Type {

            HEADER, GOAL, GOAL_COMPLETE
        }
    }

    class Header(val heading: String, val subHeading: String)

    enum class Change {

        PROGRESS
    }
}