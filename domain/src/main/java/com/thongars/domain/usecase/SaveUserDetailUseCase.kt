package com.thongars.domain.usecase

import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.LocalUserRepository
import com.thongars.utilities.ResourceState
import com.thongars.utilities.safeFetchDataCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveUserDetailUseCase @Inject constructor(
    private val localUserRepository: LocalUserRepository,
) {
        suspend operator fun invoke(user: UserDetail, dispatcher: CoroutineDispatcher): Flow<ResourceState<Unit>> {
            return safeFetchDataCall(dispatcher) {
                localUserRepository.insertUserDetail(user)
            }
        }
    }