package com.twisthenry8gmail.weeklyphoenix.view.add

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentAddGoalDoneBinding
import com.twisthenry8gmail.weeklyphoenix.util.GoalPropertyUtil
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddGoalViewModel
import kotlinx.android.synthetic.main.fragment_add_goal_done.*
import java.time.LocalDate

class FragmentAddGoalDone : Fragment() {

    private val viewModel by navGraphViewModels<AddGoalViewModel>(R.id.nav_add_goal)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAddGoalDoneBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })

        add_goal_done_start.setOnClickListener {

            StartDatePickerDialog()
                .show(childFragmentManager, null)
        }

        add_goal_done_end.setOnClickListener {

            EndDatePickerDialog()
                .show(childFragmentManager, null)
        }

        add_goal_done_increase.setOnClickListener {

            FragmentAddGoalIncrease().show(childFragmentManager, null)
        }

        add_goal_done_color.setOnClickListener {

            FragmentAddGoalColor()
                .show(childFragmentManager, null)
        }
    }

    class StartDatePickerDialog : DialogFragment() {

        private val viewModel by navGraphViewModels<AddGoalViewModel>(R.id.nav_add_goal)

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val date = LocalDate.ofEpochDay(viewModel.startDate.value)
            val dialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->

                    viewModel.startDate.value =
                        LocalDate.of(year, month + 1, dayOfMonth).toEpochDay()
                },
                date.year,
                date.monthValue - 1,
                date.dayOfMonth
            )
            dialog.datePicker.minDate = System.currentTimeMillis()

            val endDate = viewModel.endDate.value
            if (GoalPropertyUtil.hasEndDate(endDate)) {
                dialog.datePicker.maxDate = LocalDate.ofEpochDay(endDate)
                    .minus(viewModel.reset.multiple, viewModel.reset.unit)
                    .toEpochDay() * (24 * 60 * 60 * 1000)
            }

            return dialog
        }
    }

    // TODO View binding here? Better architecture solution? MaterialDatePicker?
    class EndDatePickerDialog : DialogFragment() {

        private val viewModel by navGraphViewModels<AddGoalViewModel>(R.id.nav_add_goal)

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val minDate = LocalDate.ofEpochDay(viewModel.startDate.value)
                .plus(viewModel.reset.multiple, viewModel.reset.unit)

            val endDate = viewModel.endDate.value
            val date =
                if (GoalPropertyUtil.hasEndDate(endDate)) LocalDate.ofEpochDay(endDate) else minDate

            val dialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->

                    viewModel.endDate.value =
                        LocalDate.of(year, month + 1, dayOfMonth).toEpochDay()
                },
                date.year,
                date.monthValue - 1,
                date.dayOfMonth
            )

            dialog.datePicker.minDate = minDate.toEpochDay() * (24 * 60 * 60 * 1000)

            dialog.setButton(
                AlertDialog.BUTTON_NEUTRAL,
                getString(R.string.add_goal_end_neutral)
            ) { _, _ ->

                viewModel.endDate.value = -1
            }

            return dialog
        }
    }
}