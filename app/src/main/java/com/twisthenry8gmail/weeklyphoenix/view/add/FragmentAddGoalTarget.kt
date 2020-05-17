package com.twisthenry8gmail.weeklyphoenix.view.add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.util.DateTimeUtil
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import kotlinx.android.synthetic.main.fragment_add_goal_target.*

class FragmentAddGoalTarget : Fragment(R.layout.fragment_add_goal_target) {

    private val viewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val goal = viewModel.requireCurrentGoal()

        add_goal_target.minValue = goal.type.minIncrement
        add_goal_target.increment = goal.type.minIncrement

        if (goal.type == Goal.Type.TIMED) {

            add_goal_target.textFactory = {

                DateTimeUtil.showGoalTime(
                    requireContext(),
                    it
                )
            }
        }

        add_goal_target.valueChangedListener = {

            viewModel.requireCurrentGoal().target = it
        }

        add_goal_target_continue.setOnClickListener {

            findNavController().navigate(R.id.action_fragmentAddGoalTarget_to_fragmentAddGoalReset)
        }

        add_goal_target_back.setOnClickListener {

            findNavController().popBackStack()
        }
    }
}