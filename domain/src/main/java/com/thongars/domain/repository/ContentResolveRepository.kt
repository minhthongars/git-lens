package com.thongars.domain.repository

import com.thongars.domain.model.User
import com.thongars.utilities.ResourceState
import kotlinx.coroutines.flow.Flow

interface ContentResolveRepository {
    fun getSingleUser(username: String): Flow<ResourceState<User?>>
    fun getAllUsers(): Flow<ResourceState<List<User>>>
}