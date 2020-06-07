package com.twisthenry8gmail.weeklyphoenix

open class Event<T>(private val content: T) {

    protected var consumed = false

    fun get() = content

    fun getIfNotHandled(): T? {

        return if (consumed) null else {

            consumed = true
            content
        }
    }

    class Observer<T>(val onChanged: (T) -> Unit) : androidx.lifecycle.Observer<Event<T>> {

        override fun onChanged(t: Event<T>?) {

            t?.getIfNotHandled()?.let { onChanged(it) }
        }
    }
}

class HandledEvent<T>(content: T) : Event<T>(content) {

    init {

        consumed = true
    }
}