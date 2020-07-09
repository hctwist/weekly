package com.twisthenry8gmail.weeklyphoenix.view.viewgoal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentViewGoalInfoBinding
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentViewGoalInfoBindingImpl
import com.twisthenry8gmail.weeklyphoenix.view.LinearMarginItemDecoration
import com.twisthenry8gmail.weeklyphoenix.viewmodel.ViewGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication

class FragmentViewGoalInfo : BottomSheetDialogFragment() {

    private val viewModel by viewModels<ViewGoalViewModel>(
        ownerProducer = { requireParentFragment() },
        factoryProducer = {
            ViewGoalViewModel.Factory(
                arguments,
                resources,
                weeklyApplication().goalRepository,
                weeklyApplication().goalHistoryRepository
            )
        })

    private lateinit var binding: FragmentViewGoalInfoBinding

    private val historyAdapter = GoalHistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // TODO This is a workaround - commented on bug report https://github.com/material-components/material-components-android/issues/894#event-2938594725
        binding = FragmentViewGoalInfoBinding.inflate(
            inflater.cloneInContext(
                ContextThemeWrapper(
                    context,
                    R.style.AppTheme
                )
            ), container, false
        )

        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.goal.observe(viewLifecycleOwner, Observer {

            historyAdapter.goal = it
        })

        viewModel.histories.observe(viewLifecycleOwner, Observer {

            historyAdapter.histories = it
            historyAdapter.notifyDataSetChanged()
        })

        setupHistoryList()
    }

    private fun setupHistoryList() {

        binding.viewGoalHistory.run {

            layoutManager = LinearLayoutManager(context)
            addItemDecoration(LinearMarginItemDecoration(resources.getDimension(R.dimen.margin)))
            adapter = historyAdapter
        }
    }
}