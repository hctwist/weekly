package com.twisthenry8gmail.weeklyphoenix.view.viewgoal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialContainerTransform
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentViewGoalBinding
import com.twisthenry8gmail.weeklyphoenix.util.ColorUtil
import com.twisthenry8gmail.weeklyphoenix.util.Transitions
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {

            arguments?.getInt(Transitions.CONTAINER_COLOR)?.let { startContainerColor = it }
            endContainerColor =
                ColorUtil.resolveColorAttribute(requireContext(), android.R.attr.colorBackground)
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

        viewModel.showingInfo.observe(viewLifecycleOwner, Event.Observer {

            if (it) FragmentViewGoalInfo().show(childFragmentManager, null)
        })
    }


}