package com.twisthenry8gmail.weeklyphoenix.view.add

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentAddGoalTypeBinding
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_add_goal_type.*

class FragmentAddGoalType : Fragment(R.layout.fragment_add_goal_type) {

    private val viewModel by navGraphViewModels<AddGoalViewModel>(R.id.nav_add_goal) {

        AddGoalViewModel.Factory(resources, weeklyApplication().goalRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAddGoalTypeBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })
    }
}