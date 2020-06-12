package com.twisthenry8gmail.weeklyphoenix.view.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.MainRepository
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.TaskDay
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentMain3Binding
import com.twisthenry8gmail.weeklyphoenix.view.LinearMarginItemDecoration
import com.twisthenry8gmail.weeklyphoenix.viewmodel.Main3ViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_main_3.*

class FragmentMain3 : Fragment() {

    private val viewModel by viewModels<Main3ViewModel> {
        Main3ViewModel.Factory(
            resources,
            MainRepository(requireActivity().getPreferences(Context.MODE_PRIVATE)),
            weeklyApplication().goalRepository,
            weeklyApplication().taskRepository
        )
    }

    private val goalsAdapter = GoalAdapter3()
    private val taskDaysAdapter = TaskDayAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMain3Binding.inflate(inflater, container, false)
        binding.viewmodel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupGoals()
        setupWeek()

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })

        viewModel.goals.observe(viewLifecycleOwner, Observer {

            goalsAdapter.goals = it
        })

        viewModel.goalsDiffData.observe(viewLifecycleOwner, Observer {

            it.dispatchUpdatesTo(goalsAdapter)
        })

        viewModel.taskDays.observe(viewLifecycleOwner, Observer {

            taskDaysAdapter.taskDays = it
            taskDaysAdapter.notifyDataSetChanged()
        })
    }

    private fun setupGoals() {

        goalsAdapter.clickHandler = object : GoalAdapter3.ClickHandler {

            override fun onGoalClick(goal: Goal) {

                viewModel.onGoalClick(goal)
            }

            override fun onGoalAdd() {

                viewModel.onAddGoal()
            }
        }

        main_goals.run {

            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(LinearMarginItemDecoration(resources.getDimension(R.dimen.double_margin)))
            LinearSnapHelper().attachToRecyclerView(this)
            adapter = goalsAdapter
        }
    }

    private fun setupWeek() {

        taskDaysAdapter.clickHandler = object : TaskDayAdapter.ClickHandler {

            override fun onTaskDayClick(day: TaskDay) {

                viewModel.onDayClick(day)
            }

            override fun onAddTask(day: TaskDay) {

                viewModel.onAddTask(day)
            }
        }

        main_task_days.run {

            layoutManager = LinearLayoutManager(context)
            addItemDecoration(LinearMarginItemDecoration(resources.getDimension(R.dimen.margin)))
            adapter = taskDaysAdapter
        }
    }
}