package com.thongars.domain.repository

import com.thongars.domain.model.User
import com.thongars.domain.model.UserDetail
import com.thongars.utilities.ResourceState
import kotlinx.coroutines.flow.Flow

interface RemoteUserRepository {
    fun fetchUserListing(limit: Int, since: Int): Flow<ResourceState<List<User>>>
    fun getUserDetail(username: String):  Flow<ResourceState<UserDetail>>
}