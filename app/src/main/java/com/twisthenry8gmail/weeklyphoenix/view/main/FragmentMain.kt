package com.twisthenry8gmail.weeklyphoenix.view.main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.MainRepository
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.viewmodel.MainViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_main.*

class FragmentMain : Fragment(R.layout.fragment_main) {

    private val currentGoalViewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })
    private val mainViewModel by viewModels<MainViewModel> {
        MainViewModel.Factory(
            resources,
            MainRepository(requireActivity().getPreferences(Context.MODE_PRIVATE)),
            weeklyApplication().goalRepository
        )
    }
    private lateinit var goalAdapter: GoalAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mainViewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })

        main_toolbar.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {

                R.id.main_add_goal -> {

                    mainViewModel.onAddGoal()
                    true
                }

                else -> false
            }
        }

        goalAdapter = GoalAdapter(resources)

        goalAdapter.clickHandler = object : GoalAdapter.ClickHandler {

            override fun onGoalAction(goal: Goal) {

                mainViewModel.onGoalAction(requireContext(), goal)
            }

            override fun onGoalClick(goal: Goal) {

                mainViewModel.onGoalClick(goal)
            }
        }

        main_empty_add_goal.setOnClickListener {

            main_toolbar.menu.performIdentifierAction(R.id.main_add_goal, 0)
        }

        main_cards.apply {

            addItemDecoration(GoalAdapter.ItemDecoration())
            layoutManager = LinearLayoutManager(context)
            adapter = goalAdapter
        }

        mainViewModel.goalAdapterData.observe(viewLifecycleOwner, Observer {

            goalAdapter.data = it
            main_cards.finishedLoading()
        })

        mainViewModel.goalAdapterDiffData.observe(viewLifecycleOwner, Observer {

            it.dispatchUpdatesTo(goalAdapter)
        })
    }
}