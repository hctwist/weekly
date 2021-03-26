package com.twisthenry8gmail.weeklyphoenix.view.main.defaultlayout

import android.os.Bundle
import android.os.Handler
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
import com.google.android.material.transition.Hold
import com.twisthenry8gmail.recyclerextensions.StatefulRecyclerHelper
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalSnapshot
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskSnapshot
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentMainDefaultBinding
import com.twisthenry8gmail.weeklyphoenix.view.LinearMarginItemDecoration
import com.twisthenry8gmail.weeklyphoenix.view.main.FragmentMainMenu
import com.twisthenry8gmail.weeklyphoenix.view.main.GoalAdapter
import com.twisthenry8gmail.weeklyphoenix.view.main.GoalLoadingAdapter
import com.twisthenry8gmail.weeklyphoenix.view.main.GoalsItemTouchCallback
import com.twisthenry8gmail.weeklyphoenix.view.tasks.TaskSnapshotAdapter
import com.twisthenry8gmail.weeklyphoenix.view.tasks.TaskSnapshotLoadingAdapter
import com.twisthenry8gmail.weeklyphoenix.viewmodel.MainDefaultViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication

class FragmentMainDefault : Fragment() {

    private val viewModel by viewModels<MainDefaultViewModel> {
        MainDefaultViewModel.Factory(
            resources,
            weeklyApplication().mainRepository,
            weeklyApplication().goalRepository,
            weeklyApplication().taskRepository
        )
    }

    private lateinit var binding: FragmentMainDefaultBinding

    private val goalsAdapterHelper = StatefulRecyclerHelper(GoalAdapter()).apply {

        loadingAdapter = GoalLoadingAdapter()
    }

    private val tasksAdapterHelper =
        StatefulRecyclerHelper(TaskSnapshotAdapter(MainDefaultViewModel.MAX_TASK_DISPLAY)).apply {

            loadingAdapter = TaskSnapshotLoadingAdapter()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        postponeEnterTransition()
        Handler().postDelayed({

            startPostponedEnterTransition()
        }, 1000)

        binding = FragmentMainDefaultBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupGoals()
        setupWeek()

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            exitTransition = if (arrayOf(
                    R.id.action_fragmentMain_to_fragmentViewTaskDay,
                    R.id.action_fragmentMain_to_fragmentOverdueTasks
                ).contains(it.getId())
            ) {

                Hold()
            } else {

                Hold()
            }

            it.navigateFrom(findNavController())
        })

        viewModel.showingMenu.observe(viewLifecycleOwner, Event.Observer {

            if (it) FragmentMainMenu().show(childFragmentManager, null)
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

        goalsAdapterHelper.emptyPlaceholder = binding.mainGoalsEmpty

        goalsAdapterHelper.mainAdapter.listener = object :
            GoalAdapter.Listener {

            override fun onGoalClick(goal: GoalSnapshot, view: View) {

                viewModel.onGoalClick(goal, view)
            }

            override fun onGoalAdd() {

                viewModel.onAddGoal()
            }

            override fun onGoalMove(goal: GoalSnapshot, newSortOrder: Int) {

                viewModel.onGoalMove(goal, newSortOrder)
            }
        }

        binding.mainGoals.run {

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

            override fun onCardClick(taskSnapshot: TaskSnapshot, view: View) {

                viewModel.onTaskCardClicked(taskSnapshot, view)
            }

            override fun onAdd(taskSnapshot: TaskSnapshot) {

                viewModel.onAddTask(taskSnapshot)
            }
        }

        binding.mainTaskDays.run {

            layoutManager = LinearLayoutManager(context)
            addItemDecoration(LinearMarginItemDecoration(resources.getDimension(R.dimen.margin)))
            tasksAdapterHelper.attachToRecyclerView(this)
        }
    }
}