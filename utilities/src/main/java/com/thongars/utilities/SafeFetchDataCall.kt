package com.thongars.utilities

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun <T> safeFetchDataCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T,
): Flow<ResourceState<T>> = flow {
    emit(ResourceState.Loading)
    val result = apiCall()
    emit(ResourceState.Success(result))
}
    .catch { e ->
        emit(
            ResourceState.Error(e.mapToAppError().errorMessage)
        )
    }
    .flowOn(dispatcher)