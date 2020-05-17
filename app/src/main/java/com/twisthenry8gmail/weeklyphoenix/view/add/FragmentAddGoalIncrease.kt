package com.twisthenry8gmail.weeklyphoenix.view.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import kotlinx.android.synthetic.main.fragment_periodic_increase.*
import kotlin.math.roundToInt

class FragmentAddGoalIncrease : BottomSheetDialogFragment() {

    private val viewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_periodic_increase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // TODO Way better system needed here
        val increment = viewModel.requireCurrentGoal().type.minIncrement
        val target = viewModel.requireCurrentGoal().target

        val increases = mutableSetOf<Long>()

        for (i in 0..100 step 5) {

            val multiplier = i.toDouble() / 100
            val candidateInc = ((target * multiplier) / increment).roundToInt() * increment
            increases.add(candidateInc)
        }

        increases.forEach { inc ->

            val chip = Chip(context)
            chip.text = inc.toString()
            chip.setOnClickListener {

                viewModel.requireCurrentGoal().increase = inc
                viewModel.postCurrentGoalUpdate()
                dismiss()
            }
            periodic_increase_group.addView(chip)
        }
    }
}