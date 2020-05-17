package com.twisthenry8gmail.weeklyphoenix.view.add

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentAddGoalDoneBinding
import com.twisthenry8gmail.weeklyphoenix.util.DateTimeUtil
import com.twisthenry8gmail.weeklyphoenix.util.GoalDisplayUtil
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddGoalDoneViewModel
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_add_goal_done.*
import java.time.LocalDate

class FragmentAddGoalDone : Fragment() {

    private val currentGoalViewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })
    private val viewModel by viewModels<AddGoalDoneViewModel> {
        AddGoalDoneViewModel.Factory(
            currentGoalViewModel,
            weeklyApplication().goalRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAddGoalDoneBinding.inflate(inflater)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigate(findNavController())
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

            if (goal.hasEndDate()) {
                dialog.datePicker.maxDate = LocalDate.ofEpochDay(goal.endDate)
                    .minus(goal.resetMultiple, goal.resetUnit).toEpochDay() * (24 * 60 * 60 * 1000)
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

        // TODO Should be DoneViewModel
        private val viewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val goal = viewModel.requireCurrentGoal()

            val minDate = LocalDate.now().plus(goal.resetMultiple, goal.resetUnit)
            val date = if (goal.hasEndDate()) LocalDate.ofEpochDay(goal.endDate) else minDate

            val dialog = DatePickerDialog(
                requireContext(),
                this,
                date.year,
                date.monthValue - 1,
                date.dayOfMonth
            )

            dialog.datePicker.minDate = minDate.toEpochDay() * (24 * 60 * 60 * 1000)

            // TODO Hardcoded text
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "None") { _, _ ->

                viewModel.requireCurrentGoal().endDate = -1
                viewModel.postCurrentGoalUpdate()
            }

            return dialog
        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

            viewModel.requireCurrentGoal().endDate =
                LocalDate.of(year, month + 1, dayOfMonth).toEpochDay()
            viewModel.postCurrentGoalUpdate()
        }
    }
}