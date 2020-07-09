package com.twisthenry8gmail.weeklyphoenix.view.addgoal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentAddGoalBinding
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication

class FragmentAddGoal : Fragment() {

    private val viewModel by viewModels<AddGoalViewModel> {

        AddGoalViewModel.Factory(resources, weeklyApplication().goalRepository)
    }

    private lateinit var binding: FragmentAddGoalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {

            viewModel.onBack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddGoalBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.parentNavigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateFrom(findNavController())
        })

        viewModel.childNavigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateFrom(binding.addGoalContainer.findNavController())
        })
    }

    companion object {

        fun viewModelFromChild(childFragment: Fragment): Lazy<AddGoalViewModel> {

            return childFragment.viewModels({
                childFragment.requireParentFragment().requireParentFragment()
            })
        }

        fun viewModelFromDialog(dialogFragment: DialogFragment): Lazy<AddGoalViewModel> {

            return dialogFragment.viewModels({
                dialogFragment.requireParentFragment().requireParentFragment()
                    .requireParentFragment()
            })
        }
    }
}