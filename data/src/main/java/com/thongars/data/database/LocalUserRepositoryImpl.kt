package com.thongars.data.database

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.thongars.data.database.dao.UserDao
import com.thongars.data.database.model.UserEntity
import com.thongars.data.mapper.toDomain
import com.thongars.data.mapper.toEntity
import com.thongars.domain.IoDispatcher
import com.thongars.domain.model.User
import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.LocalUserRepository
import com.thongars.utilities.ResourceState
import com.thongars.utilities.safeFetchDataCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalUserRepositoryImpl @Inject constructor(
    private val dao: UserDao,
    private val pager: Pager<Int, UserEntity>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocalUserRepository {

    override fun userPagingDataFlow(): Flow<PagingData<User>> {
        return pager.flow.map { pagingData ->
            pagingData.map { userEntity ->
                userEntity.toDomain()
            }
        }
            .flowOn(ioDispatcher)
    }

    override fun insertUserDetail(user: UserDetail): Flow<ResourceState<Unit>> {
        return safeFetchDataCall(
            dispatcher = ioDispatcher,
            fetchDataCall = {
                dao.insertUserDetail(user.toEntity())
            }
        )
    }

    override fun getUserDetail(username: String): Flow<ResourceState<UserDetail?>> {
        return safeFetchDataCall(
            dispatcher = ioDispatcher,
            fetchDataCall = {
                val user = dao.getUser(username)
                dao.getUserDetail(username)?.toDomain(user!!)
            }
        )

    }

    override fun getUser(username: String): Flow<ResourceState<User?>> {
        return safeFetchDataCall(
            dispatcher = ioDispatcher,
            fetchDataCall = {
                dao.getUser(username)?.toDomain()
            }
        )
    }

    override fun getAllUserDetail(): Flow<UserDetail?> {
        val userDetailEntitiesFlow = dao.getAllUserDetail()
        return userDetailEntitiesFlow.flatMapLatest { userDetailEntities ->
            flow {
                userDetailEntities.map { userDetailEntity ->
                    val username = userDetailEntity.login
                    val user = dao.getUser(username)
                    emit(dao.getUserDetail(username)?.toDomain(user!!))
                }
            }
        }
            .flowOn(ioDispatcher)
    }

}