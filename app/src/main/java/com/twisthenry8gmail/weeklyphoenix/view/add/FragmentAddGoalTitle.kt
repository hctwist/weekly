package com.twisthenry8gmail.weeklyphoenix.view.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentAddGoalTitleBinding
import com.twisthenry8gmail.weeklyphoenix.util.hideSoftKeyboard
import com.twisthenry8gmail.weeklyphoenix.util.showSoftKeyboard
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddGoalViewModel
import kotlinx.android.synthetic.main.fragment_add_goal_title.*

class FragmentAddGoalTitle : Fragment() {

    private val viewModel by navGraphViewModels<AddGoalViewModel>(R.id.nav_add_goal)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAddGoalTitleBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // TODO Showing and hiding of the keyboard
        add_goal_title.showSoftKeyboard()

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })
    }

    override fun onDetach() {
        super.onDetach()
        hideSoftKeyboard()
    }
}