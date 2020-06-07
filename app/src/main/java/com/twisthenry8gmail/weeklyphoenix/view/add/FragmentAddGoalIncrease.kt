package com.twisthenry8gmail.weeklyphoenix.view.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentPeriodicIncreaseBinding
import com.twisthenry8gmail.weeklyphoenix.util.GoalDisplayUtil
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddGoalViewModel
import kotlinx.android.synthetic.main.fragment_periodic_increase.*

class FragmentAddGoalIncrease : BottomSheetDialogFragment() {

    private val viewModel by navGraphViewModels<AddGoalViewModel>(R.id.nav_add_goal)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentPeriodicIncreaseBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}