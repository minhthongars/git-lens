package com.thongars.domain.usecase

import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.RemoteUserRepository
import com.thongars.utilities.ResourceState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserDetailUseCase @Inject constructor(
    private val remoteUserRepository: RemoteUserRepository,
) {

    suspend operator fun invoke(username: String): Flow<ResourceState<UserDetail>> {
        return remoteUserRepository.getUserDetail(username)
    }
}