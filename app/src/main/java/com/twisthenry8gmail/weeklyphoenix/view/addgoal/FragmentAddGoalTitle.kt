package com.twisthenry8gmail.weeklyphoenix.view.addgoal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentAddGoalTitleBinding
import com.twisthenry8gmail.weeklyphoenix.util.showSoftKeyboard
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication

class FragmentAddGoalTitle : Fragment() {

    private val viewModel by viewModels<AddGoalViewModel>(
        { requireParentFragment().requireParentFragment() },
        { AddGoalViewModel.Factory(resources, weeklyApplication().goalRepository) }
    )

    private lateinit var binding: FragmentAddGoalTitleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddGoalTitleBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.addGoalTitle.showSoftKeyboard()
    }
}