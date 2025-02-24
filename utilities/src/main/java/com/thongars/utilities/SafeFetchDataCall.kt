package com.thongars.utilities

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

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