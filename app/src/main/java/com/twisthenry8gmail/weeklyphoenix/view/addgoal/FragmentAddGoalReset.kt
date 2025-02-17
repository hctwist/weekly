package com.twisthenry8gmail.weeklyphoenix.view.addgoal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentAddGoalResetBinding
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddGoalViewModel

class FragmentAddGoalReset : Fragment() {

    private val viewModel by viewModels<AddGoalViewModel>({ requireParentFragment().requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAddGoalResetBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel

        return binding.root
    }
}