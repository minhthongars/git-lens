package com.thongars.domain.usecase

import com.thongars.domain.mapper.toDomain
import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllLocalUserDetailUseCase @Inject constructor(
    private val localUserRepository: LocalUserRepository,
) {

    operator fun invoke(): Flow<List<UserDetail>?> {
        val userDetail = localUserRepository.getAllUserDetail()
        return userDetail.map { detailList ->
            detailList.map { detail ->
                detail.toDomain(localUserRepository.getUser(detail.login)!!)
            }
        }
    }
}