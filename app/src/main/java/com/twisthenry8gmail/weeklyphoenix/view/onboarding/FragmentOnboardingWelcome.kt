package com.twisthenry8gmail.weeklyphoenix.view.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.twisthenry8gmail.weeklyphoenix.MainRepository
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentOnboardingWelcomeBinding
import com.twisthenry8gmail.weeklyphoenix.viewmodel.OnboardingViewModel

class FragmentOnboardingWelcome : Fragment() {

    private val viewModel by viewModels<OnboardingViewModel>(
        ownerProducer = { requireParentFragment() },
        factoryProducer = {
            OnboardingViewModel.Factory(
                MainRepository((requireActivity().getPreferences(Context.MODE_PRIVATE)))
            )
        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentOnboardingWelcomeBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel

        return binding.root
    }
}