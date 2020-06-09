package com.twisthenry8gmail.weeklyphoenix.view.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.twisthenry8gmail.weeklyphoenix.*
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentMain2Binding
import com.twisthenry8gmail.weeklyphoenix.viewmodel.Main2ViewModel
import kotlinx.android.synthetic.main.fragment_main_2.*

class FragmentMain2 : Fragment() {

    private val viewModel by viewModels<Main2ViewModel> {
        Main2ViewModel.Factory(
            resources,
            MainRepository(requireActivity().getPreferences(Context.MODE_PRIVATE)),
            weeklyApplication().goalRepository
        )
    }

    private val goalAdapter = GoalAdapter2()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMain2Binding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })

        goalAdapter.clickHandler = object : GoalAdapter2.ClickHandler {

            override fun onGoalAction(goal: Goal, card: View) {

                viewModel.onGoalAction(requireContext(), goal, card)
            }

            override fun onGoalClick(goal: Goal) {

                viewModel.onGoalClick(goal)
            }
        }

        viewModel.goals.observe(viewLifecycleOwner, Observer {

            goalAdapter.goals = it
        })

        viewModel.goalsDiffData.observe(viewLifecycleOwner, Observer {

            if (it == null) goalAdapter.notifyDataSetChanged()
            else it.dispatchUpdatesTo(goalAdapter)
        })

        viewModel.page.observe(viewLifecycleOwner, Event.Observer {

            main_goals.currentItem = it
        })

        setupGoalsPager()
    }

    private fun setupGoalsPager() {

        main_goals.apply {

            // TODO Waiting for a fix
            (getChildAt(0) as RecyclerView).overScrollMode = overScrollMode

            adapter = goalAdapter
            offscreenPageLimit = 1
            setPageTransformer(MainPageTransformer(context))
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {

                    viewModel.onPageSelected(position)
                }
            })
        }

        main_goals.setCurrentItem(viewModel.page.value.get(), false)
        main_goals.post { main_goals.requestTransform() }
    }
}