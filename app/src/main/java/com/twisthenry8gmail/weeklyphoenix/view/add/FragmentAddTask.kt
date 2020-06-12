package com.twisthenry8gmail.weeklyphoenix.view.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.twisthenry8gmail.buttons.AccessibleTouchImageButton
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentAddTaskBinding
import com.twisthenry8gmail.weeklyphoenix.util.showSoftKeyboard
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddTaskViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_add_task.*
import kotlinx.android.synthetic.main.task_card.*

class FragmentAddTask : Fragment() {

    private val viewModel by viewModels<AddTaskViewModel> {

        AddTaskViewModel.Factory(arguments, weeklyApplication().taskRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        add_task_input.showSoftKeyboard()

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })
    }

    companion object {

        const val DATE = "date"

        fun buildArgs(date: Long): Bundle {

            return Bundle().apply {

                putLong(DATE, date)
            }
        }
    }
}