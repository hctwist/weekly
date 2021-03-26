package com.twisthenry8gmail.weeklyphoenix.view.viewgoal

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils.loadAnimation
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.ChangeTransform
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.vectordrawable.graphics.drawable.AnimationUtilsCompat
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialContainerTransform
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentViewGoalBinding
import com.twisthenry8gmail.weeklyphoenix.util.ColorUtil
import com.twisthenry8gmail.weeklyphoenix.util.Transitions
import com.twisthenry8gmail.weeklyphoenix.util.popIn
import com.twisthenry8gmail.weeklyphoenix.view.LinearMarginItemDecoration
import com.twisthenry8gmail.weeklyphoenix.viewmodel.ViewGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication

class FragmentViewGoal : Fragment() {

    private val viewModel by viewModels<ViewGoalViewModel> {
        ViewGoalViewModel.Factory(
            arguments,
            resources,
            weeklyApplication().goalRepository,
            weeklyApplication().goalHistoryRepository
        )
    }

    private lateinit var binding: FragmentViewGoalBinding

    private val historyAdapter = GoalHistoryAdapter()

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    sharedElementEnterTransition = MaterialContainerTransform().apply {

        duration = 7000
        scrimColor = Color.TRANSPARENT
        arguments?.getInt(Transitions.CONTAINER_COLOR)?.let { startContainerColor = it }
        endContainerColor =
            ColorUtil.resolveColorAttribute(requireContext(), android.R.attr.colorBackground)

        addListener(object: TransitionListenerAdapter() {

            override fun onTransitionStart(transition: Transition) {

                binding.viewGoalIncrement.alpha = 0F
            }

            override fun onTransitionEnd(transition: Transition) {

                binding.viewGoalIncrement.popIn()
            }
        })
    }
}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentViewGoalBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            if (it.getId() == R.id.action_fragmentViewGoal_to_fragmentGoalTimer) {

                // TODO
//                it.setNavigatorExtras(
//                    FragmentNavigatorExtras(
//
//                        view_goal_title to Transitions.Names.VIEW_GOAL_TITLE,
//                        view_goal_progress_text to Transitions.Names.VIEW_GOAL_PROGRESS
//                    )
//                )
            }
            it.navigateFrom(findNavController())
        })

        viewModel.goal.observe(viewLifecycleOwner, Observer {

            historyAdapter.goal = it
        })

        viewModel.histories.observe(viewLifecycleOwner, Observer {

            historyAdapter.histories = it
            historyAdapter.notifyDataSetChanged()
        })

        setupHistoryList()
    }

    private fun setupHistoryList() {

        binding.viewGoalHistory.run {

            layoutManager = LinearLayoutManager(context)
            addItemDecoration(LinearMarginItemDecoration(resources.getDimension(R.dimen.margin)))
            adapter = historyAdapter
        }
    }
}