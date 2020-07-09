package com.twisthenry8gmail.weeklyphoenix.view.goaltiming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentGoalTimingBinding
import com.twisthenry8gmail.weeklyphoenix.services.GoalTimerService
import com.twisthenry8gmail.weeklyphoenix.util.Transitions
import com.twisthenry8gmail.weeklyphoenix.viewmodel.GoalTimerViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import java.util.concurrent.TimeUnit

class FragmentGoalTiming : Fragment() {

    private val viewModel by viewModels<GoalTimerViewModel> {
        GoalTimerViewModel.Factory(
            weeklyApplication().goalRepository
        )
    }

    private lateinit var binding: FragmentGoalTimingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val timerService = Intent(context, GoalTimerService::class.java)
        requireContext().startForegroundService(timerService)

        // TODO This transition is not working
        sharedElementEnterTransition =
            Transitions.initialiseEnterTransition(requireContext(), AutoTransition())

        postponeEnterTransition(500, TimeUnit.MILLISECONDS)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGoalTimingBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateFrom(findNavController())
        })

        viewModel.timingStopped.observe(viewLifecycleOwner, Event.Observer {

            if (it) {

                requireContext().stopService(Intent(context, GoalTimerService::class.java))
            }
        })
    }
}