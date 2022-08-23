package com.vibs.weatherapisdk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <RequestType>
</RequestType> */
abstract class NetworkBoundResource<RequestType>
@MainThread constructor(coroutineScope: CoroutineScope) {

    private val result = MediatorLiveData<Resource<RequestType>>()

    init {
        result.value = Resource.loading(null)
        coroutineScope.launch {
            fetchFromNetwork()
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<RequestType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork() {
        val apiResponse = createCall()
        apiResponse?.let {
            result.addSource(apiResponse) { response ->
                result.removeSource(apiResponse)
                when (response) {
                    is ApiSuccessResponse -> {
                        setValue(Resource.success(processResponse(response)))
                    }

                    is ApiErrorResponse -> {
                        onFetchFailed()
                        setValue(Resource.error(response.errorMessage, null))
                    }
                    else -> {
                        //Empty body
                    }
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<RequestType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>?
}
