package com.twisthenry8gmail.weeklyphoenix.view

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentGoalSuccessBinding
import com.twisthenry8gmail.weeklyphoenix.util.ColorUtil
import com.twisthenry8gmail.weeklyphoenix.viewmodel.GoalSuccessViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_goal_success.*
import kotlinx.android.synthetic.main.fragment_goal_success_circle.*
import kotlinx.coroutines.launch

class FragmentGoalSuccess : Fragment(R.layout.fragment_goal_success) {

    private val viewModel by viewModels<GoalSuccessViewModel> {
        GoalSuccessViewModel.Factory(
            arguments,
            weeklyApplication().goalRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentGoalSuccessBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private val animationHandler = Handler()
    private val animationRunnable = Runnable {

        goal_success_progress.target = 1
        goal_success_progress.setProgress(1, true) {

            viewModel.shouldAnimateViews = false

            goal_success_done.apply {

                visibility = View.VISIBLE
                scaleX = 0F
                scaleY = 0F
                animate().scaleX(1F).scaleY(1F)
                    .setInterpolator(OvershootInterpolator(6F)).setDuration(400).start()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })

        if (viewModel.shouldAnimateViews) {

            animationHandler.postDelayed(animationRunnable, 150)
        }
    }

    override fun onDestroyView() {

        animationHandler.removeCallbacks(animationRunnable)
        super.onDestroyView()
    }
}