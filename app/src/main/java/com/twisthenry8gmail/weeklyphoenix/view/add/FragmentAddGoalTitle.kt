package com.twisthenry8gmail.weeklyphoenix.view.add

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.util.hideSoftKeyboard
import com.twisthenry8gmail.weeklyphoenix.util.showSoftKeyboard
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddGoalTitleViewModel
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_add_goal_title.*

class FragmentAddGoalTitle : Fragment(R.layout.fragment_add_goal_title) {

    private val currentGoalViewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })

    private val viewModel by viewModels<AddGoalTitleViewModel> {
        AddGoalTitleViewModel.Factory(
            weeklyApplication().goalRepository,
            currentGoalViewModel
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        add_goal_title.showSoftKeyboard()

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigate(findNavController())
        })

        add_goal_title.addTextChangedListener {

            viewModel.onTextChanged(it)
        }

        add_goal_title.setText(currentGoalViewModel.requireCurrentGoal().name)

        viewModel.canContinue.observe(viewLifecycleOwner, Observer {

            add_goal_title_continue.isEnabled = it
        })

        add_goal_title_continue.setOnClickListener {

            viewModel.onContinue()
        }

        add_goal_title_back.setOnClickListener {

            hideSoftKeyboard()
            viewModel.onBack()
        }
    }
}