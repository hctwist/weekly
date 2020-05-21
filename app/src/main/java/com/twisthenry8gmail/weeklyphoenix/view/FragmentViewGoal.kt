package com.twisthenry8gmail.weeklyphoenix.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentViewGoalBinding
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.viewmodel.ViewGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_view_goal.*

class FragmentViewGoal : Fragment() {

    private val currentGoalViewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })
    private val viewModel by viewModels<ViewGoalViewModel> {
        ViewGoalViewModel.Factory(
            resources,
            weeklyApplication().goalRepository,
            currentGoalViewModel
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentViewGoalBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigate(findNavController())
        })

        view_goal_toolbar.setOnMenuItemClickListener {

            viewModel.onMenuItemClick(it)
        }
    }
}