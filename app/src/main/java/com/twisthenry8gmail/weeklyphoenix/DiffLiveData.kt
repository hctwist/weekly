package com.twisthenry8gmail.weeklyphoenix

import android.view.animation.PathInterpolator
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.*

abstract class DiffLiveData<TI, TO>(
    source: LiveData<List<TI>>,
    processingScope: CoroutineScope
) : MediatorLiveData<DiffLiveData.DiffData<TO>>() {

    private var oldDataCache: List<TO>? = null

    private var processingJob: Job? = null

    init {

        addSource(source) { newData ->

            processingJob?.cancel()
            processingJob = processingScope.launch(Dispatchers.Default) {

                val oldData = oldDataCache
                val mappedNewData = map(newData)

                ensureActive()

                val diff = if (oldData != null) {

                    DiffUtil.calculateDiff(getCallback(oldData, mappedNewData))
                } else null

                ensureActive()

                oldDataCache = mappedNewData
                postValue(DiffData(mappedNewData, diff))
            }
        }
    }

    protected abstract fun getCallback(oldData: List<TO>, newData: List<TO>): DiffUtil.Callback

    protected abstract fun map(data: List<TI>): List<TO>

    class DiffData<T>(val data: List<T>, val diffData: DiffUtil.DiffResult?)
}