package com.thongars.domain.usecase

import androidx.paging.PagingData
import com.thongars.domain.model.User
import com.thongars.domain.repository.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchUserUseCase @Inject constructor(
    private val localUserRepository: LocalUserRepository
) {

    operator fun invoke(): Flow<PagingData<User>> {
        return localUserRepository
            .userPagingDataFlow()
    }
}