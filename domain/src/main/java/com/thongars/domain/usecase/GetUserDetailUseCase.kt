package com.thongars.domain.usecase

import com.thongars.domain.mapper.toDomain
import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.RemoteUserRepository
import com.thongars.utilities.ResourceState
import com.thongars.utilities.safeFetchDataCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserDetailUseCase @Inject constructor(
    private val remoteUserRepository: RemoteUserRepository,
) {

    suspend operator fun invoke(username: String, dispatcher: CoroutineDispatcher): Flow<ResourceState<UserDetail>> {
        return safeFetchDataCall(dispatcher = dispatcher) {
            remoteUserRepository.getUserDetail(username).toDomain()
        }
    }
}