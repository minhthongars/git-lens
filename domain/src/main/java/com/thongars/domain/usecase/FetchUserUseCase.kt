package com.thongars.domain.usecase

import androidx.paging.PagingData
import com.thongars.domain.model.User
import com.thongars.domain.repository.LocalUserRepository
import kotlinx.coroutines.flow.Flow

class FetchUserUseCase(
    private val localUserRepository: LocalUserRepository
) {

    operator fun invoke(): Flow<PagingData<User>> {
        return localUserRepository.userPagingDataFlow()
    }
}