package com.thongars.utilities

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

sealed class ResourceState<out T> {

    /**
     * Loading state, typically used when fetching data.
     */
    data object Loading : ResourceState<Nothing>()

    /**
     * Represents a successful state with the data of type [T].
     */
    data class Success<T>(val data: T) : ResourceState<T>()

    /**
     * Represents an error state with an optional [message].
     */
    data class Error(val message: String) : ResourceState<Nothing>()

    /**
     * Represents an empty state, typically used when no data is available.
     */
    data object Empty : ResourceState<Nothing>()
}

suspend fun <T> handleResourceState(
    resourceState: ResourceState<T>,
    onSuccess: suspend (T) -> Unit = {},
    onError: (String) -> Unit = {},
    onLoading: () -> Unit = {}
) {
    when (resourceState) {
        is ResourceState.Success -> onSuccess(resourceState.data)
        is ResourceState.Error -> onError(resourceState.message)
        is ResourceState.Loading -> onLoading()
        else -> Unit
    }
}