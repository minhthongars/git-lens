package com.thongars.domain.usecase

import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.LocalUserRepository
import kotlinx.coroutines.flow.Flow

class GetAllLocalUserDetailUseCase(
    private val localUserRepository: LocalUserRepository,
) {

    operator fun invoke(): Flow<List<UserDetail>> {
        return localUserRepository.getAllUserDetail()
    }
}