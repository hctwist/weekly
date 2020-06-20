package com.twisthenry8gmail.weeklyphoenix

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class AggregatorLiveData<S1, S2, O>(
    source1: LiveData<S1>,
    source2: LiveData<S2>,
    private val aggregate: (S1, S2) -> O
) : MediatorLiveData<O>() {

    private var s1Result: S1? = null
    private var s2Result: S2? = null

    init {

        addSource(source1) {

            s1Result = it
            onChanged()
        }

        addSource(source2) {

            s2Result = it
            onChanged()
        }
    }

    private fun onChanged() {

        s1Result?.let { s1R ->

            s2Result?.let { s2R ->

                value = aggregate(s1R, s2R)
            }
        }
    }
}