package com.twisthenry8gmail.weeklyphoenix.view.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.viewmodel.OnboardingViewModel
import com.twisthenry8gmail.weeklyphoenix.weeklyApplication
import kotlinx.android.synthetic.main.fragment_onboarding.*

class FragmentOnboarding : Fragment(R.layout.fragment_onboarding) {

    private val viewModel by viewModels<OnboardingViewModel>(
        factoryProducer = {
            OnboardingViewModel.Factory(
                weeklyApplication().mainRepository
            )
        })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        onboarding_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {

                viewModel.onPageSelected(position)
            }
        })

        viewModel.navigationCommander.observe(viewLifecycleOwner, Event.Observer {

            it.navigateFrom(findNavController())
        })

        onboarding_pager.adapter = Adapter(this)

        viewModel.page.observe(viewLifecycleOwner, Observer {

            onboarding_pager.currentItem = it
        })
    }

    class Adapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {

            return OnboardingData.values().size
        }

        override fun createFragment(position: Int): Fragment {

            return FragmentOnboardingBlueprint.getInstance(OnboardingData.values()[position])
        }
    }
}