package com.thongars.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class RemoteUser(
    val id: Int?,
    val login: String?,
    @field:Json(name = "avatar_url")
    val avatarUrl: String?,
    @field:Json(name = "html_url")
    val htmlUrl: String?,
)