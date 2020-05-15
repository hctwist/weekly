package com.twisthenry8gmail.weeklyphoenix.view.add

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.RoomModel
import com.twisthenry8gmail.weeklyphoenix.util.DateTimeUtil
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import kotlinx.android.synthetic.main.fragment_add_goal_done.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class FragmentAddGoalDone : Fragment(R.layout.fragment_add_goal_done) {

    private val viewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.currentGoal.observe(viewLifecycleOwner, Observer {

            add_goal_done_start.text =
                DateTimeUtil.displayDate(
                    it.startDate
                )

            if (it.endDate < 0) {

                add_goal_done_end.setText(R.string.goal_no_end)
            } else {

                add_goal_done_end.text =
                    DateTimeUtil.displayDate(
                        it.endDate
                    )
            }

            add_goal_done_color.setBackgroundColor(it.color)
        })

        add_goal_done_start.setOnClickListener {

            StartDatePickerDialog()
                .show(childFragmentManager, null)
        }

        add_goal_done_end.setOnClickListener {

            EndDatePickerDialog()
                .show(childFragmentManager, null)
        }

        add_goal_done_color.setOnClickListener {

            FragmentAddGoalColor()
                .show(childFragmentManager, null)
        }

        add_goal_done_more.setOnClickListener {

            val constraintSet = ConstraintSet().apply {

                clone(add_goal_done_root)
                setVisibility(
                    R.id.add_goal_done_more,
                    View.GONE
                )
                setVisibility(
                    R.id.add_goal_done_more_group,
                    View.VISIBLE
                )
            }

            TransitionManager.beginDelayedTransition(
                add_goal_done_root
            )

            constraintSet.applyTo(add_goal_done_root)
        }

        add_goal_done_confirm.setOnClickListener {

            viewModel.viewModelScope.launch {
                RoomModel.getInstance(
                    requireContext()
                ).goalsDao()
                    .addGoal(viewModel.requireCurrentGoal())
            }
            findNavController().navigate(R.id.action_fragmentAddGoalDone_to_fragmentMain)
        }
    }

    class StartDatePickerDialog : DialogFragment(),
        DatePickerDialog.OnDateSetListener {

        private val viewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val goal = viewModel.requireCurrentGoal()

            val date = LocalDate.ofEpochDay(goal.startDate)
            val dialog = DatePickerDialog(
                requireContext(),
                this,
                date.year,
                date.monthValue - 1,
                date.dayOfMonth
            )
            dialog.datePicker.minDate = System.currentTimeMillis()

            if (goal.endDate >= 0) {
                dialog.datePicker.maxDate = LocalDate.ofEpochDay(goal.endDate)
                    .minus(goal.resetMultiple, goal.resetUnit).toEpochDay() * (24 * 60 * 60 * 1000)
            }

            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Test") { dialog, which ->


            }

            return dialog
        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

            viewModel.requireCurrentGoal().startDate =
                LocalDate.of(year, month + 1, dayOfMonth).toEpochDay()
            viewModel.postCurrentGoalUpdate()
        }
    }

    class EndDatePickerDialog : DialogFragment(),
        DatePickerDialog.OnDateSetListener {

        private val viewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val goal = viewModel.requireCurrentGoal()

            val minDate = LocalDate.now().plus(goal.resetMultiple, goal.resetUnit)
            val date = if (goal.endDate >= 0) LocalDate.ofEpochDay(goal.endDate) else minDate

            val dialog = DatePickerDialog(
                requireContext(),
                this,
                date.year,
                date.monthValue - 1,
                date.dayOfMonth
            )

            dialog.datePicker.minDate = minDate.toEpochDay() * (24 * 60 * 60 * 1000)

            return dialog
        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

            viewModel.requireCurrentGoal().endDate =
                LocalDate.of(year, month + 1, dayOfMonth).toEpochDay()
            viewModel.postCurrentGoalUpdate()
        }
    }
}