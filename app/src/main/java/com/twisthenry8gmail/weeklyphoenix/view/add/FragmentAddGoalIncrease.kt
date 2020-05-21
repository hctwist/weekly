package com.twisthenry8gmail.weeklyphoenix.view.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.util.GoalDisplayUtil
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import kotlinx.android.synthetic.main.fragment_periodic_increase.*

// TODO Improve with MVVM
class FragmentAddGoalIncrease : BottomSheetDialogFragment() {

    private val viewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })

    private var increment: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_periodic_increase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        increment = viewModel.requireCurrentGoal().type.minIncrement

        viewModel.currentGoal.observe(viewLifecycleOwner, Observer {

            periodic_increase_value.text =
                GoalDisplayUtil.displayProgressValue(requireContext(), it.type, it.increase)
        })

        periodic_increase_minus.setOnClickListener {

            onDecrement()
        }

        periodic_increase_plus.setOnClickListener {

            onIncrement()
        }
    }

    private fun onDecrement() {

        val goal = viewModel.requireCurrentGoal()

        if (goal.increase >= increment) {

            goal.increase -= increment
            viewModel.postCurrentGoalUpdate()
        }
    }

    private fun onIncrement() {

        viewModel.requireCurrentGoal().increase += increment
        viewModel.postCurrentGoalUpdate()
    }
}