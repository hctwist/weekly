package com.twisthenry8gmail.weeklyphoenix.view.success

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.viewmodel.GoalSuccess2ViewModel
import com.twisthenry8gmail.weeklyphoenix.viewmodel.GoalSuccessViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_goal_success_2.*

class FragmentGoalSuccess2 : Fragment(R.layout.fragment_goal_success_2) {

    private val viewModel by viewModels<GoalSuccess2ViewModel> {
        GoalSuccess2ViewModel.Factory(
            arguments,
            weeklyApplication().goalRepository
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.goal.observe(viewLifecycleOwner, Observer {

            goal_success_controller.setGoal(it)
        })

        goal_success_controller.postDelayed({

            startPostponedEnterTransition()
        }, 1000)
    }
}