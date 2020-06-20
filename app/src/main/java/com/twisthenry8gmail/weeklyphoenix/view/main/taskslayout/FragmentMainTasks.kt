package com.twisthenry8gmail.weeklyphoenix.view.main.taskslayout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.twisthenry8gmail.recyclerextensions.StatefulRecyclerHelper
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskSnapshot
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentMainTasksBinding
import com.twisthenry8gmail.weeklyphoenix.view.LinearMarginItemDecoration
import com.twisthenry8gmail.weeklyphoenix.view.main.TaskSnapshotAdapter
import com.twisthenry8gmail.weeklyphoenix.view.main.TaskSnapshotLoadingAdapter
import com.twisthenry8gmail.weeklyphoenix.viewmodel.MainTasksViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_main_tasks.*
import java.time.*
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalField
import java.util.*

// TODO Change naming of everything 'Main' to 'MainDefault'
class FragmentMainTasks : Fragment() {

    private val viewModel by viewModels<MainTasksViewModel> {
        MainTasksViewModel.Factory(
            weeklyApplication().taskRepository
        )
    }

    private val tasksAdapterHelper = StatefulRecyclerHelper(TaskSnapshotAdapter()).apply {

        loadingAdapter = TaskSnapshotLoadingAdapter()
    }

    private val monthDisplayNames by lazy {
        Month.values().map { it.getDisplayName(TextStyle.FULL, Locale.getDefault()) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMainTasksBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupDropdownButtons()
        setupList()

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })

        viewModel.tasks.observe(viewLifecycleOwner, Observer {

            tasksAdapterHelper.mainAdapter.taskSnapshots = it
            tasksAdapterHelper.mainAdapter.notifyDataSetChanged()

            tasksAdapterHelper.loading = false
        })
    }

    private fun setupDropdownButtons() {

        main_tasks_month.setOnClickListener { v ->

            val monthPopupMenu = PopupMenu(context, v)

            monthDisplayNames.forEach {

                monthPopupMenu.menu.add(it)
            }

            monthPopupMenu.setOnMenuItemClickListener {

                val index = monthDisplayNames.indexOf(it.title)
                viewModel.onMonthSelected(Month.values()[index])

                true
            }

            monthPopupMenu.show()
        }

        main_tasks_year.setOnClickListener { v ->

            val yearPopupMenu = PopupMenu(context, v)

            val installTime = requireContext().packageManager.getPackageInfo(
                requireContext().packageName,
                0
            ).firstInstallTime

            val startYear = Instant.ofEpochMilli(installTime).atZone(ZoneId.systemDefault()).year

            repeat(10) {

                yearPopupMenu.menu.add((startYear + it).toString())
            }

            yearPopupMenu.setOnMenuItemClickListener {

                viewModel.onYearSelected(it.title.toString().toInt())

                true
            }

            yearPopupMenu.show()
        }
    }

    private fun setupList() {

        tasksAdapterHelper.mainAdapter.clickHandler = object :
            TaskSnapshotAdapter.ClickHandler {

            override fun onCardClick(taskSnapshot: TaskSnapshot) {

                viewModel.onClick(taskSnapshot)
            }

            override fun onAdd(taskSnapshot: TaskSnapshot) {

                viewModel.onAdd(taskSnapshot)
            }
        }

        main_tasks_list.run {

            layoutManager = LinearLayoutManager(context)
            addItemDecoration(LinearMarginItemDecoration(context.resources.getDimension(R.dimen.margin)))
            tasksAdapterHelper.attachToRecyclerView(this)
        }
    }
}