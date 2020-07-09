package com.twisthenry8gmail.weeklyphoenix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.twisthenry8gmail.weeklyphoenix.data.MainRepository
import com.twisthenry8gmail.weeklyphoenix.NonNullMutableLiveData
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.view.onboarding.OnboardingData
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel

class OnboardingViewModel(private val mainRepository: MainRepository) : NavigatorViewModel() {

    private val _page = NonNullMutableLiveData(0)
    val page: LiveData<Int>
        get() = _page

    fun next() {

        if (page.value == OnboardingData.values().size - 1) {

            finish()
        } else {

            _page.value++
        }
    }

    fun onPageSelected(position: Int) {

        _page.value = position
    }

    fun finish() {

        mainRepository.setNotFirstTime()
        navigateTo(R.id.action_global_fragmentMain)
    }

    class Factory(private val mainRepository: MainRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return OnboardingViewModel(mainRepository) as T
            }

            throw IllegalArgumentException()
        }
    }
}