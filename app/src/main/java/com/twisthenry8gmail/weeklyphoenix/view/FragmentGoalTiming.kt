package com.twisthenry8gmail.weeklyphoenix.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentGoalTimingBinding
import com.twisthenry8gmail.weeklyphoenix.viewmodel.GoalTimerViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_goal_timing.*

class FragmentGoalTiming : Fragment() {

    private val viewModel by viewModels<GoalTimerViewModel> {
        GoalTimerViewModel.Factory(
            weeklyApplication().goalRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition =
            Transitions.initialiseEnterTransition(requireContext(), ChangeBounds())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentGoalTimingBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            if (it.getId() == R.id.action_fragmentGoalTimer_to_fragmentViewGoal) {

                it.setNavigatorExtras(
                    FragmentNavigatorExtras(

                        goal_timing_title to Transitions.ViewGoal.TRANSITION_NAME_TITLE,
                        goal_timing_progress to Transitions.ViewGoal.TRANSITION_NAME_PROGRESS
                    )
                )
            }
            it.navigateWith(findNavController())
        })

        viewModel.timingStopped.observe(viewLifecycleOwner, Event.Observer {

            if (it) {

                goal_timing_stop.shrink(object : ExtendedFloatingActionButton.OnChangedCallback() {

                    override fun onShrunken(extendedFab: ExtendedFloatingActionButton?) {

                        goal_timing_stop.setText(R.string.goal_timing_done)
                        goal_timing_stop.extend()
                    }
                })
            }
        })

        if (viewModel.timingStopped.value?.get() == true) {

            goal_timing_stop.setText(R.string.goal_timing_done)
            goal_timing_stop.isExtended = false
        }
    }
}