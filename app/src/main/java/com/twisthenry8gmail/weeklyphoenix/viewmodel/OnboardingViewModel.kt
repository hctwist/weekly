package com.twisthenry8gmail.weeklyphoenix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.twisthenry8gmail.weeklyphoenix.data.MainRepository
import com.twisthenry8gmail.weeklyphoenix.NonNullMutableLiveData
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel

class OnboardingViewModel(private val mainRepository: MainRepository) : NavigatorViewModel() {

    private val _page = NonNullMutableLiveData(0)
    val page: LiveData<Int>
        get() = _page

    fun next() {

        _page.value++
    }

    fun previous() {

        _page.value--
    }

    fun finish() {

        mainRepository.setNotFirstTime()
        navigateBack()
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