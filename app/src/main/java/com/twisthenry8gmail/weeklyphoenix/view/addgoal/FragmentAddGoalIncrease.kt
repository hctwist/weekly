package com.twisthenry8gmail.weeklyphoenix.view.addgoal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentPeriodicIncreaseBinding

class FragmentAddGoalIncrease : BottomSheetDialogFragment() {

    private val viewModel by FragmentAddGoal.viewModelFromDialog(this)

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