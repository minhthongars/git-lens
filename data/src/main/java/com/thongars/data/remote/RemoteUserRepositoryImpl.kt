package com.thongars.data.remote

import com.thongars.data.mapper.toDomain
import com.thongars.data.remote.model.RemoteUser
import com.thongars.data.remote.model.RemoteUserDetail
import com.thongars.domain.model.User
import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.RemoteUserRepository
import com.thongars.utilities.ResourceState
import com.thongars.utilities.safeFetchDataCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class RemoteUserRepositoryImpl(
    private val client: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) : RemoteUserRepository {

    override suspend fun fetchUserListing(limit: Int, since: Int): List<User> {
        val userResponses: List<RemoteUser> = client.get("${ApiConstant.BASE_URL}/users") {
            parameter("per_page", limit)
            parameter("since", since)
        }.body()
        return userResponses.map { it.toDomain() }
    }

    override suspend fun getUserDetail(username: String): Flow<ResourceState<UserDetail>> {
        return safeFetchDataCall(
            dispatcher = ioDispatcher,
            fetchDataCall = {
                val userResponse: RemoteUserDetail = client.get("${ApiConstant.BASE_URL}/users/$username").body()
                userResponse.toDomain()
            }
        )
    }

}