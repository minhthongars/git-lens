package com.thongars.domain.usecase

import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllLocalUserDetailUseCase @Inject constructor(
    private val localUserRepository: LocalUserRepository,
) {

    operator fun invoke(): Flow<UserDetail?> {
        return localUserRepository.getAllUserDetail()
    }
}