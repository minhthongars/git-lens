package com.thongars.domain.repository

import com.thongars.data.remote.GitHubApi
import com.thongars.data.remote.model.RemoteUser
import com.thongars.data.remote.model.RemoteUserDetail
import javax.inject.Inject

class RemoteUserRepository @Inject constructor(
    private val gitHubApi: GitHubApi
) {

    suspend fun fetchUserListing(limit: Int, since: Int): List<RemoteUser> {
        return gitHubApi.getUsers(perPage = limit, since = since)
    }

    suspend fun getUserDetail(username: String): RemoteUserDetail {
        return gitHubApi.getUserDetail(username)
    }

}