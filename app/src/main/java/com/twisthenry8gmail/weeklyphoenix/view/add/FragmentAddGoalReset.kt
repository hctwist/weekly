package com.twisthenry8gmail.weeklyphoenix.view.add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import kotlinx.android.synthetic.main.fragment_add_goal_reset.*
import java.time.LocalDate

class FragmentAddGoalReset : Fragment(R.layout.fragment_add_goal_reset) {

    private val viewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Goal.ResetPreset.values().forEach { preset ->

            val chip = Chip(context)
            chip.setText(preset.displayNameRes)

            chip.setOnClickListener {

                add_goal_frequency_display.animateText(chip.text)
                viewModel.requireCurrentGoal().setResetPreset(preset)
            }

            add_goal_frequency_chips.addView(chip)

            if (viewModel.requireCurrentGoal().hasResetPreset(preset)) {

                add_goal_frequency_display.setText(preset.displayNameRes)
                chip.isChecked = true
            }
        }

        add_goal_frequency_continue.setOnClickListener {

            viewModel.requireCurrentGoal()
                .updateResetDate(LocalDate.ofEpochDay(viewModel.requireCurrentGoal().startDate))
            findNavController().navigate(R.id.action_fragmentAddGoalReset_to_fragmentAddGoalDone)
        }

        add_goal_frequency_back.setOnClickListener {

            findNavController().popBackStack()
        }
    }
}