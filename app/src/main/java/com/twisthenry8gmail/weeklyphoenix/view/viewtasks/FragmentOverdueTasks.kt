package com.twisthenry8gmail.weeklyphoenix.view.viewtasks

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
import com.twisthenry8gmail.weeklyphoenix.data.tasks.Task
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentOverdueTasksBinding
import com.twisthenry8gmail.weeklyphoenix.view.LinearMarginItemDecoration
import com.twisthenry8gmail.weeklyphoenix.viewmodel.OverdueTasksViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_overdue_tasks.*

class FragmentOverdueTasks : Fragment() {

    private val viewModel by viewModels<OverdueTasksViewModel>() {

        OverdueTasksViewModel.Factory(weeklyApplication().taskRepository)
    }

    private val taskAdapter = TaskAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentOverdueTasksBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupList()

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })

        viewModel.overdueDiffData.observe(viewLifecycleOwner, Observer {

            taskAdapter.tasks = it.data
            if (it.diffData == null) {
                taskAdapter.notifyDataSetChanged()
            } else {
                it.diffData.dispatchUpdatesTo(taskAdapter)
            }
        })

        taskAdapter.clickHandler = object : TaskAdapter.ClickHandler {

            override fun onTaskCompleteChanged(task: Task, complete: Boolean) {

                viewModel.onTaskComplete(task)
            }
        }
    }

    private fun setupList() {

        overdue_tasks_tasks.run {

            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
            addItemDecoration(LinearMarginItemDecoration(resources.getDimension(R.dimen.margin)))
        }
    }
}