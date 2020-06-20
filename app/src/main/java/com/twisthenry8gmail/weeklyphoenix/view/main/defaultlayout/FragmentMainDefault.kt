package com.twisthenry8gmail.weeklyphoenix.view.main.defaultlayout

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.twisthenry8gmail.recyclerextensions.StatefulRecyclerHelper
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.MainRepository
import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskSnapshot
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentMainDefaultBinding
import com.twisthenry8gmail.weeklyphoenix.view.LinearMarginItemDecoration
import com.twisthenry8gmail.weeklyphoenix.view.main.GoalAdapter
import com.twisthenry8gmail.weeklyphoenix.view.main.GoalLoadingAdapter
import com.twisthenry8gmail.weeklyphoenix.view.main.GoalsItemTouchCallback
import com.twisthenry8gmail.weeklyphoenix.view.main.TaskSnapshotAdapter
import com.twisthenry8gmail.weeklyphoenix.viewmodel.MainDefaultViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_main_default.*

class FragmentMainDefault : Fragment() {

    private val viewModel by viewModels<MainDefaultViewModel> {
        MainDefaultViewModel.Factory(
            resources,
            MainRepository(
                requireActivity().getPreferences(Context.MODE_PRIVATE)
            ),
            weeklyApplication().goalRepository,
            weeklyApplication().taskRepository
        )
    }

    private val goalsAdapterHelper = StatefulRecyclerHelper(GoalAdapter()).apply {

        loadingAdapter = GoalLoadingAdapter()
    }

    private val tasksAdapterHelper = StatefulRecyclerHelper(TaskSnapshotAdapter()).apply {

        loadingAdapter = TaskSnapshotAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMainDefaultBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupGoals()
        setupWeek()

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })

        viewModel.goalsDiffData.observe(viewLifecycleOwner, Observer {

            goalsAdapterHelper.mainAdapter.goals = it.data
            goalsAdapterHelper.loading = false

            it.diffData?.run { dispatchUpdatesTo(goalsAdapterHelper.mainAdapter) }
        })

        viewModel.taskSnapshots.observe(viewLifecycleOwner, Observer {

            tasksAdapterHelper.mainAdapter.taskSnapshots = it
            tasksAdapterHelper.mainAdapter.notifyDataSetChanged()
            tasksAdapterHelper.loading = false
        })
    }

    private fun setupGoals() {

        goalsAdapterHelper.mainAdapter.listener = object :
            GoalAdapter.Listener {

            override fun onGoalClick(goal: Goal) {

                viewModel.onGoalClick(goal)
            }

            override fun onGoalAdd() {

                viewModel.onAddGoal()
            }

            override fun onGoalMove(goal: Goal, newSortOrder: Int) {

                viewModel.onGoalMove(goal, newSortOrder)
            }
        }

        main_goals.run {

            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(LinearMarginItemDecoration(resources.getDimension(R.dimen.double_margin)))
            LinearSnapHelper().attachToRecyclerView(this)
            ItemTouchHelper(GoalsItemTouchCallback()).attachToRecyclerView(this)
            goalsAdapterHelper.attachToRecyclerView(this)
        }
    }

    private fun setupWeek() {

        tasksAdapterHelper.mainAdapter.clickHandler = object :
            TaskSnapshotAdapter.ClickHandler {

            override fun onCardClick(taskSnapshot: TaskSnapshot) {

                viewModel.onTaskCardClicked(taskSnapshot)
            }

            override fun onAdd(taskSnapshot: TaskSnapshot) {

                viewModel.onAddTask(taskSnapshot)
            }
        }

        main_task_days.run {

            layoutManager = LinearLayoutManager(context)
            addItemDecoration(LinearMarginItemDecoration(resources.getDimension(R.dimen.margin)))
            tasksAdapterHelper.attachToRecyclerView(this)
        }
    }
}