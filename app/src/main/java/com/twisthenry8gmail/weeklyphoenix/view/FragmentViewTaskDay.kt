package com.twisthenry8gmail.weeklyphoenix.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Task
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentViewTaskDayBinding
import com.twisthenry8gmail.weeklyphoenix.viewmodel.ViewTaskDayViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_view_task_day.*

class FragmentViewTaskDay : Fragment() {

    private val viewModel by viewModels<ViewTaskDayViewModel> {

        ViewTaskDayViewModel.Factory(
            arguments,
            weeklyApplication().taskRepository
        )
    }

    private val tasksAdapter = TaskAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentViewTaskDayBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })

        viewModel.taskDay.observe(viewLifecycleOwner, Observer {

            tasksAdapter.tasks = it.tasks
            tasksAdapter.notifyDataSetChanged()
        })

        tasksAdapter.clickHandler = object : TaskAdapter.ClickHandler {

            override fun onToggleTaskComplete(task: Task) {

                viewModel.onToggleTaskComplete(task)
            }
        }

        setupTasks()
    }

    private fun setupTasks() {

        view_task_day_tasks.run {

            layoutManager = LinearLayoutManager(context)
            addItemDecoration(LinearMarginItemDecoration(resources.getDimension(R.dimen.margin)))
            adapter = tasksAdapter
        }
    }
}