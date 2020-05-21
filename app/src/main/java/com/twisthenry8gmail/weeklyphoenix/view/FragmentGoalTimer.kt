package com.twisthenry8gmail.weeklyphoenix.view

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.util.GoalDisplayUtil
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.viewmodel.GoalTimerViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_timed_goal.*

class FragmentGoalTimer : Fragment(R.layout.fragment_timed_goal) {

    private val viewModel by viewModels<GoalTimerViewModel> {
        GoalTimerViewModel.Factory(
            weeklyApplication().goalRepository
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.goal.observe(viewLifecycleOwner, Observer {

            timed_goal_name.text = it.name
        })

        viewModel.getDuration().observe(viewLifecycleOwner, Observer {

            timed_goal_time.text = GoalDisplayUtil.displayGoalTime(requireContext(), it / 1000)
        })

        timed_goal_stop.setOnClickListener {

            stopTimer()
        }
    }

    private fun stopTimer() {

        viewModel.stopTimer(requireContext())

        viewModel.progressUpdate.observe(viewLifecycleOwner, Observer {

            if (it.updatedGoal.isComplete()) {

                ViewModelProvider(requireActivity())[CurrentGoalViewModel::class.java].currentGoal.value =
                    it.updatedGoal
                findNavController().navigate(R.id.fragmentMain)
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentGoalSuccess)
            } else {

                timed_goal_progress.setColor(it.updatedGoal.color)
                timed_goal_progress.target = it.updatedGoal.target
                timed_goal_progress.setProgress(it.oldProgress, false)

                val fadeTransition = Fade().apply {

                    addListener(object : TransitionListenerAdapter() {

                        override fun onTransitionEnd(transition: Transition) {

                            timed_goal_progress.setProgress(it.updatedGoal.progress, true) {

                                Handler().postDelayed(
                                    { findNavController().navigate(R.id.action_fragmentGoalTimer_to_fragmentMain) },
                                    1000
                                )
                            }
                        }
                    })
                }

                val constraintSet = ConstraintSet().apply { clone(timed_goal_root) }
                constraintSet.setVisibility(R.id.timed_goal_stop, View.INVISIBLE)
                constraintSet.setVisibility(R.id.timed_goal_progress, View.VISIBLE)
                TransitionManager.beginDelayedTransition(timed_goal_root, fadeTransition)
                constraintSet.applyTo(timed_goal_root)
            }
        })
    }
}