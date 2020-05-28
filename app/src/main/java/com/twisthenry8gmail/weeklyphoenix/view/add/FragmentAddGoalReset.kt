package com.twisthenry8gmail.weeklyphoenix.view.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.chip.Chip
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentAddGoalResetBinding
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddGoalViewModel
import kotlinx.android.synthetic.main.fragment_add_goal_reset.*

class FragmentAddGoalReset : Fragment() {

    private val viewModel by navGraphViewModels<AddGoalViewModel>(R.id.nav_add_goal)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAddGoalResetBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })

        // TODO Data bind this somehow? Best practice? Maybe move to custom ChipGroup view?
        Goal.ResetPreset.values().forEach { preset ->

            val chip = Chip(context)
            chip.setText(preset.displayNameRes)

            chip.setOnClickListener {

                add_goal_frequency_display.animateText(chip.text)
                viewModel.reset = preset.toReset()
            }

            add_goal_frequency_chips.addView(chip)

            if (viewModel.reset.isPreset(preset)) {

                add_goal_frequency_display.setText(preset.displayNameRes)
                chip.isChecked = true
            }
        }
    }
}