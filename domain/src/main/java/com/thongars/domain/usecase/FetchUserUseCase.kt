package com.thongars.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.thongars.data.database.model.UserEntity
import com.thongars.domain.mapper.toDomain
import com.thongars.domain.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchUserUseCase @Inject constructor(
    private val pager: Pager<Int, UserEntity>
) {

    operator fun invoke(dispatcher: CoroutineDispatcher): Flow<PagingData<User>> {
        return pager
            .flow
            .map { pagingData ->
                pagingData.map {
                    it.toDomain()
                }
            }
            .flowOn(dispatcher)
    }
}