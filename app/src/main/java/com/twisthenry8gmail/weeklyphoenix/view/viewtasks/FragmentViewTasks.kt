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
import com.twisthenry8gmail.recyclerextensions.StatefulRecyclerHelper
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.tasks.Task
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentViewTasksBinding
import com.twisthenry8gmail.weeklyphoenix.view.LinearMarginItemDecoration
import com.twisthenry8gmail.weeklyphoenix.viewmodel.ViewTasksViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_view_tasks.*

class FragmentViewTasks : Fragment() {

    private val viewModel by viewModels<ViewTasksViewModel> {

        ViewTasksViewModel.Factory(
            arguments,
            weeklyApplication().taskRepository
        )
    }

    private val tasksAdapterHelper = StatefulRecyclerHelper(TaskAdapter())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentViewTasksBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })

        viewModel.diffData.observe(viewLifecycleOwner, Observer {

            tasksAdapterHelper.mainAdapter.tasks = it.data
            tasksAdapterHelper.loading = false

            it.diffData?.run { dispatchUpdatesTo(tasksAdapterHelper.mainAdapter) }
        })

        setupTasks()
    }

    private fun setupTasks() {

        tasksAdapterHelper.mainAdapter.clickHandler = object :
            TaskAdapter.ClickHandler {

            override fun onTaskCompleteChanged(task: Task, complete: Boolean) {

                viewModel.onTaskCompleteChanged(task, complete)
            }
        }

        tasksAdapterHelper.emptyPlaceholder = view_tasks_empty

        view_tasks_tasks.run {

            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                LinearMarginItemDecoration(
                    resources.getDimension(R.dimen.margin)
                )
            )
            tasksAdapterHelper.attachToRecyclerView(this)
        }
    }
}