package com.twisthenry8gmail.weeklyphoenix.view.onboarding

import android.content.Context
import android.os.Bundle
import android.renderscript.Allocation
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.twisthenry8gmail.weeklyphoenix.data.MainRepository
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentOnboardingBlueprintBinding
import com.twisthenry8gmail.weeklyphoenix.viewmodel.OnboardingViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication

class FragmentOnboardingBlueprint : Fragment() {

    private val viewModel by viewModels<OnboardingViewModel>(
        ownerProducer = { requireParentFragment() },
        factoryProducer = {
            OnboardingViewModel.Factory(
                weeklyApplication().mainRepository
            )
        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentOnboardingBlueprintBinding.inflate(inflater, container, false)
        binding.onboardingdata = OnboardingData.values()[arguments?.getInt(ONBOARDING_DATA)!!]
        binding.viewmodel = viewModel

        return binding.root
    }

    companion object {

        private const val ONBOARDING_DATA = "data"

        fun getInstance(data: OnboardingData) = FragmentOnboardingBlueprint().apply {

            arguments = Bundle().apply {

                putInt(ONBOARDING_DATA, data.ordinal)
            }
        }
    }
}