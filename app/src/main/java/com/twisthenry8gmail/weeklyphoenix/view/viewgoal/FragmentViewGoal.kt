package com.twisthenry8gmail.weeklyphoenix.view.viewgoal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentViewGoalBinding
import com.twisthenry8gmail.weeklyphoenix.view.MarginItemDecoration
import com.twisthenry8gmail.weeklyphoenix.viewmodel.ViewGoal2ViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_view_goal.*

class FragmentViewGoal : Fragment() {

    private val viewModel by viewModels<ViewGoal2ViewModel> {
        ViewGoal2ViewModel.Factory(
            arguments,
            resources,
            weeklyApplication().goalRepository,
            weeklyApplication().goalHistoryRepository
        )
    }

    private val historyAdapter = GoalHistoryAdapter()

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

            it.navigateWith(findNavController())
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

        with(view_goal_history) {

            layoutManager = LinearLayoutManager(context)
            addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.margin)))
            adapter = historyAdapter
        }
    }
}