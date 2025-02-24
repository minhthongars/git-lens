package com.thongars.domain.usecase

import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.LocalUserRepository
import com.thongars.utilities.ResourceState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveUserDetailUseCase @Inject constructor(
    private val localUserRepository: LocalUserRepository,
) {
        operator fun invoke(user: UserDetail): Flow<ResourceState<Unit>> {
            return localUserRepository.insertUserDetail(user)
        }
    }