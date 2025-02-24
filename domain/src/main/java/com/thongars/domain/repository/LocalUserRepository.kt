package com.thongars.domain.repository

import androidx.paging.PagingData
import com.thongars.domain.model.User
import com.thongars.domain.model.UserDetail
import com.thongars.utilities.ResourceState
import kotlinx.coroutines.flow.Flow

interface LocalUserRepository {

    fun userPagingDataFlow(): Flow<PagingData<User>>

    fun insertUserDetail(user: UserDetail): Flow<ResourceState<Unit>>

    fun getUserDetail(username: String): Flow<ResourceState<UserDetail?>>

    fun getUser(username: String): Flow<ResourceState<User?>>

    fun getAllUserDetail(): Flow<UserDetail?>

}