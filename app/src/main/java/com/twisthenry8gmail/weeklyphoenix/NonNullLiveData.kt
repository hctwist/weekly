package com.twisthenry8gmail.weeklyphoenix

import androidx.lifecycle.LiveData

open class NonNullLiveData<T>(initialValue: T) : LiveData<T>() {

    constructor(valueProvider: () -> T) : this(valueProvider())

    init {

        value = initialValue
    }

    override fun getValue(): T {

        return super.getValue()!!
    }
}

class NonNullMutableLiveData<T>(initialValue: T) : NonNullLiveData<T>(initialValue) {

    constructor(valueProvider: () -> T) : this(valueProvider())

    public override fun setValue(value: T) {
        super.setValue(value)
    }

    public override fun postValue(value: T) {
        super.postValue(value)
    }
}