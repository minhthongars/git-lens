package com.thongars.data.remote

import com.thongars.data.mapper.toDomain
import com.thongars.domain.IoDispatcher
import com.thongars.domain.model.User
import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.RemoteUserRepository
import com.thongars.utilities.ResourceState
import kotlinx.coroutines.CoroutineDispatcher
import com.thongars.utilities.safeFetchDataCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteUserRepositoryImpl @Inject constructor(
    private val gitHubApi: GitHubApi,
    private val ioDispatcher: CoroutineDispatcher,
) : RemoteUserRepository {
    override fun fetchUserListing(limit: Int, since: Int): Flow<ResourceState<List<User>>> {
        return safeFetchDataCall(
            dispatcher = ioDispatcher,
            fetchDataCall = {
                gitHubApi.getUsers(perPage = limit, since = since).map { user ->
                    user.toDomain()
                }
            }
        )
    }

    override fun getUserDetail(username: String): Flow<ResourceState<UserDetail>> {
        return safeFetchDataCall(
            dispatcher = ioDispatcher,
            fetchDataCall = {
                gitHubApi.getUserDetail(login = username).toDomain()
            }
        )
    }


}