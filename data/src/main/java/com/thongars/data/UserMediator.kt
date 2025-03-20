package com.thongars.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.thongars.data.database.dao.UserDao
import com.thongars.data.database.model.UserEntity
import com.thongars.data.mapper.toEntity
import com.thongars.data.remote.ApiConstant
import com.thongars.domain.repository.RemoteUserRepository
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalPagingApi::class)
class UserMediator(
    private val remoteUserRepository: RemoteUserRepository,
    private val userDao: UserDao
): RemoteMediator<Int, UserEntity>() {

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(ApiConstant.USER_CACHE_TIME, TimeUnit.MINUTES)

        val lastInsertionTime = userDao.getInsertionTime() ?: 0

        return if (System.currentTimeMillis() - lastInsertionTime < cacheTimeout) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            runBlocking {
                userDao.clearAll()
            }
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
    ): MediatorResult {
        try {
            val since: Int = when (loadType) {
                LoadType.REFRESH -> {
                    val lastItemOrder = state.lastItemOrNull()?.order
                    lastItemOrder ?: 0
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    lastItem?.order ?: return MediatorResult.Success(endOfPaginationReached = false)
                }
            }

            val users = remoteUserRepository.fetchUserListing(
                limit = state.config.pageSize,
                since = since
            ).map { user ->
                user.toEntity()
            }

            if (loadType == LoadType.REFRESH && since == 0) {
                runBlocking {
                    userDao.clearAll()
                }
            }

            userDao.upsertAll(users)

            val endOfPaginationReached = users.size < state.config.pageSize
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}