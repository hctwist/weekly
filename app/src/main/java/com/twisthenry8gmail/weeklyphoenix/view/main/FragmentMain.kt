package com.twisthenry8gmail.weeklyphoenix.view.main

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.twisthenry8gmail.weeklyphoenix.*
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.launch

class FragmentMain : Fragment(R.layout.fragment_main) {

    private val currentGoalViewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })
    private val mainViewModel by viewModels<MainViewModel> {
        MainViewModel.Factory(
            resources,
            weeklyApplication().goalRepository,
            currentGoalViewModel
        )
    }
    private lateinit var goalAdapter: GoalAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mainViewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigate(findNavController())
        })

        main_toolbar.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {

                R.id.main_add_goal -> {

                    val popupMenu = PopupMenu(context, view.findViewById(menuItem.itemId))
                    Goal.Type.values().forEach {

                        popupMenu.menu.add(it.displayNameRes)
                    }

                    popupMenu.setOnMenuItemClickListener { popupMenuItem ->

                        mainViewModel.onAddGoal(
                            requireContext(), Goal.Type.values()
                                .find { it.getDisplayName(requireContext()) == popupMenuItem.title.toString() }!!
                        )
                        true
                    }
                    popupMenu.show()

                    true
                }

                else -> false
            }
        }

        goalAdapter = GoalAdapter(requireContext())

        goalAdapter.goalClickListener = {

            mainViewModel.onGoalClick(it)
        }

        goalAdapter.goalActionListener = { goal, _, _ ->

            mainViewModel.onGoalAction(requireContext(), goal)
        }

        main_cards.apply {

            addItemDecoration(GoalAdapter.ItemDecoration())
            layoutManager = LinearLayoutManager(context)
            adapter = goalAdapter
        }

        mainViewModel.goalAdapterData.observe(viewLifecycleOwner, Observer {

            goalAdapter.data = it
        })

        mainViewModel.goalAdapterDiffData.observe(viewLifecycleOwner, Observer {

            it.dispatchUpdatesTo(goalAdapter)
        })
    }
}