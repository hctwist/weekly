package com.twisthenry8gmail.weeklyphoenix.view.onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.MainRepository
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.viewmodel.OnboardingViewModel
import kotlinx.android.synthetic.main.fragment_onboarding.*

class FragmentOnboarding : Fragment(R.layout.fragment_onboarding) {

    private val viewModel by viewModels<OnboardingViewModel>(
        factoryProducer = {
            OnboardingViewModel.Factory(
                MainRepository((requireActivity().getPreferences(Context.MODE_PRIVATE)))
            )
        })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateWith(findNavController())
        })

        onboarding_pager.adapter = Adapter(this)

        viewModel.page.observe(viewLifecycleOwner, Observer {

            onboarding_pager.currentItem = it
        })
    }

    class Adapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {

            return 1
        }

        override fun createFragment(position: Int): Fragment {

            return when (position) {

                0 -> FragmentOnboardingIntro()

                else -> throw IllegalArgumentException()
            }
        }
    }
}