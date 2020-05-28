package com.twisthenry8gmail.weeklyphoenix.view

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.util.ColorUtil
import kotlinx.android.synthetic.main.fragment_goal_success.*
import kotlinx.coroutines.launch

class FragmentGoalSuccess : Fragment(R.layout.fragment_goal_success) {

    private val goalViewModel by activityViewModels<CurrentGoalViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val goal = goalViewModel.requireCurrentGoal()

        goal_success_name.text = goal.title
        goal_success_progress.target = 1
        goal_success_progress.setBackingArcColor(ColorUtil.lightenGoalColor(goal.color))
        goal_success_progress.setColor(goal.color)

        goal_success_done.setOnClickListener {

            findNavController().popBackStack()
        }


        Handler().postDelayed(
            {
                goal_success_progress.setProgress(1, true) {

                    animateDone()
                }

            }, 100
        )
    }

    override fun onDestroyView() {

        // TODO Cancel handler?
        super.onDestroyView()
    }

    private fun animateDone() {

        goal_success_done.apply {

            visibility = View.VISIBLE
            scaleX = 0F
            scaleY = 0F
            animate().scaleX(1F).scaleY(1F)
                .setInterpolator(OvershootInterpolator(6F)).setDuration(400).start()
        }
    }
}