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
import com.google.android.material.transition.MaterialContainerTransform
import com.twisthenry8gmail.recyclerextensions.StatefulRecyclerHelper
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.tasks.Task
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentViewTasksBinding
import com.twisthenry8gmail.weeklyphoenix.util.ColorUtil
import com.twisthenry8gmail.weeklyphoenix.util.Transitions
import com.twisthenry8gmail.weeklyphoenix.view.LinearMarginItemDecoration
import com.twisthenry8gmail.weeklyphoenix.viewmodel.ViewTasksViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication

class FragmentViewTasks : Fragment() {

    private val viewModel by viewModels<ViewTasksViewModel> {

        ViewTasksViewModel.Factory(
            arguments,
            weeklyApplication().taskRepository
        )
    }

    private lateinit var binding: FragmentViewTasksBinding

    private val tasksAdapterHelper = StatefulRecyclerHelper(TaskAdapter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {

            arguments?.getInt(Transitions.CONTAINER_COLOR)?.let { startContainerColor = it }
            endContainerColor =
                ColorUtil.resolveColorAttribute(requireContext(), android.R.attr.colorBackground)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentViewTasksBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel

        binding.executePendingBindings()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateFrom(findNavController())
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

        tasksAdapterHelper.emptyPlaceholder = binding.viewTasksEmpty

        binding.viewTasksTasks.run {

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