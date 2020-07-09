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
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.tasks.Task
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentOverdueTasksBinding
import com.twisthenry8gmail.weeklyphoenix.util.ColorUtil
import com.twisthenry8gmail.weeklyphoenix.util.Transitions
import com.twisthenry8gmail.weeklyphoenix.view.LinearMarginItemDecoration
import com.twisthenry8gmail.weeklyphoenix.viewmodel.OverdueTasksViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication

class FragmentOverdueTasks : Fragment() {

    private val viewModel by viewModels<OverdueTasksViewModel>() {

        OverdueTasksViewModel.Factory(weeklyApplication().taskRepository)
    }

    private lateinit var binding: FragmentOverdueTasksBinding

    private val taskAdapter = TaskAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {

            arguments?.getInt(Transitions.CONTAINER_COLOR)?.let { startContainerColor = it }
            endContainerColor =
                ColorUtil.resolveColorAttribute(requireContext(), android.R.attr.colorBackground)
        }
        sharedElementReturnTransition = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOverdueTasksBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.executePendingBindings()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupList()

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateFrom(findNavController())
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

        binding.overdueTasksTasks.run {

            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
            addItemDecoration(LinearMarginItemDecoration(resources.getDimension(R.dimen.margin)))
        }
    }
}