package com.twisthenry8gmail.weeklyphoenix.view.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentAddGoalTargetBinding
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddGoalViewModel

class FragmentAddGoalTarget : Fragment() {

    private val viewModel by viewModels<AddGoalViewModel>({ requireParentFragment().requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAddGoalTargetBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        return binding.root
    }
}