package com.twisthenry8gmail.weeklyphoenix.view.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.util.GoalDisplayUtil
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddGoalViewModel
import kotlinx.android.synthetic.main.fragment_periodic_increase.*

// TODO Improve with MVVM
class FragmentAddGoalIncrease : BottomSheetDialogFragment() {

    private val viewModel by navGraphViewModels<AddGoalViewModel>(R.id.nav_add_goal)

    private var increment: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_periodic_increase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        increment = viewModel.type.minIncrement

        viewModel.increase.observe(viewLifecycleOwner, Observer {

            periodic_increase_value.text =
                GoalDisplayUtil.displayProgressValue(resources, viewModel.type, it)
        })

        periodic_increase_minus.setOnClickListener {

            onDecrement()
        }

        periodic_increase_plus.setOnClickListener {

            onIncrement()
        }
    }

    private fun onDecrement() {

        val currentIncrease = viewModel.increase.value
        if (currentIncrease >= increment) {

            viewModel.increase.value = currentIncrease + increment
        }
    }

    private fun onIncrement() {

        val currentIncrease = viewModel.increase.value
        viewModel.increase.value = currentIncrease + increment
    }
}