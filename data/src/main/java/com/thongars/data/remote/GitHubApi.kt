package com.thongars.data.remote

import com.thongars.data.remote.model.RemoteUser
import com.thongars.data.remote.model.RemoteUserDetail
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {

    @GET("users")
    suspend fun getUsers(
        @Query("per_page") perPage: Int,
        @Query("since") since: Int
    ): List<RemoteUser>

    @GET("users/{login}")
    suspend fun getUserDetail(
        @Path("login") login: String
    ): RemoteUserDetail

}