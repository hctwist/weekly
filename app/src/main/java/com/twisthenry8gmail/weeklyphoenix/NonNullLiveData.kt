package com.twisthenry8gmail.weeklyphoenix

import androidx.lifecycle.MutableLiveData

class NonNullLiveData<T>(initialValue: T) : MutableLiveData<T>() {

    constructor(valueProvider: () -> T) : this(valueProvider())

    init {

        value = initialValue
    }

    override fun getValue(): T {

        return super.getValue()!!
    }
}