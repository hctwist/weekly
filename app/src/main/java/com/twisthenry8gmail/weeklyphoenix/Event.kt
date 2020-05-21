package com.twisthenry8gmail.weeklyphoenix

class Event<T>(val content: T) {

    private var consumed = false

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

class AnimatableData<T>(val data: T, val animate: Boolean)