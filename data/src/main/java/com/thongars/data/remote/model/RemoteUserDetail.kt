package com.thongars.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class RemoteUserDetail(
    val id: Int?,
    val login: String?,
    @field:Json(name = "avatar_url")
    val avatarUrl: String?,
    @field:Json(name = "html_url")
    val htmlUrl: String?,
    val location: String?,
    val followers: Int?,
    val following: Int?,
    val blog: String?
)