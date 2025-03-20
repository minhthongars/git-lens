package com.thongars.utilities

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> safeFetchDataCall(
    dispatcher: CoroutineDispatcher,
    fetchDataCall: suspend () -> T,
): Flow<ResourceState<T>> {
    return flow {
        emit(ResourceState.Loading)
        val result = fetchDataCall()
        emit(ResourceState.Success(result))
    }
        .catch { e ->
            val errorMessage = e.mapToAppError().errorMessage
            emit(ResourceState.Error(errorMessage))
        }
        .flowOn(dispatcher)
}

fun <T> Flow<T>.safeFetchDataCall(dispatcher: CoroutineDispatcher): Flow<ResourceState<T>> {
    return this
        .map<T, ResourceState<T>> { ResourceState.Success(it) }
        .onStart { emit(ResourceState.Loading) }
        .catch { e ->
            emit(ResourceState.Error(e.mapToAppError().errorMessage))
        }
        .flowOn(dispatcher)
}