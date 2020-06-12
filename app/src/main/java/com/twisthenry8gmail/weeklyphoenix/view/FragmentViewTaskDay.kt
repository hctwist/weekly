package com.twisthenry8gmail.weeklyphoenix.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentViewTaskDayBinding
import com.twisthenry8gmail.weeklyphoenix.viewmodel.ViewTaskDayViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication

class FragmentViewTaskDay : Fragment() {

    private val viewModel by viewModels<ViewTaskDayViewModel> {

        ViewTaskDayViewModel.Factory(
            arguments,
            weeklyApplication().taskRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentViewTaskDayBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.taskDay.observe(viewLifecycleOwner, Observer {

            // TODO
        })
    }
}