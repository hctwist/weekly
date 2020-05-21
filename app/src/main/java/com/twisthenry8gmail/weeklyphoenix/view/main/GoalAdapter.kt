package com.twisthenry8gmail.weeklyphoenix.view.main

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.OnRebindCallback
import androidx.recyclerview.widget.RecyclerView
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.databinding.GoalCardBinding
import com.twisthenry8gmail.weeklyphoenix.databinding.GoalCardCompleteBinding
import com.twisthenry8gmail.weeklyphoenix.databinding.GoalCardScheduledBinding
import com.twisthenry8gmail.weeklyphoenix.databinding.MainHeaderBinding
import com.twisthenry8gmail.weeklyphoenix.util.GoalDisplayUtil

class GoalAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = listOf<Data>()

    private val goalSubtitleGenerator = GoalDisplayUtil.SubtitleGenerator(context)

    var clickHandler: ClickHandler? = null

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int {

        return data[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        return when (Data.Type.values()[viewType]) {

            Data.Type.HEADER -> HeaderVH(MainHeaderBinding.inflate(inflater, parent, false))
            Data.Type.GOAL -> GoalVH(GoalCardBinding.inflate(inflater, parent, false))
            Data.Type.GOAL_COMPLETE -> GoalCompleteVH(
                GoalCardCompleteBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            Data.Type.GOAL_SCHEDULED -> GoalScheduledVH(
                GoalCardScheduledBinding.inflate(
                    inflater,
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

                        // TODO Better method to stop the rebind?
                        val callback = object : OnRebindCallback<GoalCardBinding>() {

                            override fun onPreBind(binding: GoalCardBinding?): Boolean {

                                binding?.goalProgressView?.setProgress(goal.progress, true)
                                binding?.goalSubtitle?.text =
                                    goalSubtitleGenerator.generateSubtitle(goal)
                                binding?.removeOnRebindCallback(this)

                                return false
                            }
                        }
                        h.binding.addOnRebindCallback(callback)
                        h.binding.goal = goal
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = data[position]

        when (Data.Type.values()[getItemViewType(position)]) {

            Data.Type.HEADER -> (holder as HeaderVH).bind(item.asHeader())
            Data.Type.GOAL -> (holder as GoalVH).bind(
                item.asGoal(),
                goalSubtitleGenerator,
                clickHandler!!
            )
            Data.Type.GOAL_COMPLETE -> (holder as GoalCompleteVH).bind(
                item.asGoal(),
                clickHandler!!
            )
            Data.Type.GOAL_SCHEDULED -> (holder as GoalScheduledVH).bind(
                item.asGoal(),
                clickHandler!!
            )
        }
    }

    class HeaderVH(private val binding: MainHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(header: Header) {

            binding.header = header
            binding.executePendingBindings()
        }
    }

    class GoalVH(val binding: GoalCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            goal: Goal,
            subtitleGenerator: GoalDisplayUtil.SubtitleGenerator,
            clickHandler: ClickHandler
        ) {

            binding.goal = goal
            binding.subtitlegenrator = subtitleGenerator
            binding.clickhandler = clickHandler
            binding.executePendingBindings()
        }
    }

    class GoalCompleteVH(private val binding: GoalCardCompleteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: Goal, clickHandler: ClickHandler) {

            binding.goal = goal
            binding.clickhandler = clickHandler
            binding.executePendingBindings()
        }
    }

    class GoalScheduledVH(private val binding: GoalCardScheduledBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: Goal, clickHandler: ClickHandler) {

            binding.goal = goal
            binding.clickhandler = clickHandler
            binding.executePendingBindings()
        }
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
            } else if (adapterPosition != 1) {

                outRect.top = parent.resources.getDimensionPixelSize(R.dimen.margin)
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

            fun ofGoal(type: Type, g: Goal): Data {

                return Data(type).apply {

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

            HEADER, GOAL, GOAL_COMPLETE, GOAL_SCHEDULED
        }
    }

    data class Header(val heading: String, val subHeading: String)

    enum class Change {

        PROGRESS
    }

    interface ClickHandler {

        fun onGoalAction(goal: Goal)

        fun onGoalClick(goal: Goal)
    }
}