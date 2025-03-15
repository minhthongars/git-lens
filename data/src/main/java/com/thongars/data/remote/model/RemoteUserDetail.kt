package com.thongars.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteUserDetail(
    val id: Int?,
    val login: String?,
    @SerialName("avatar_url")
    val avatarUrl: String?,
    @SerialName("html_url")
    val htmlUrl: String?,
    val location: String?,
    val followers: Int?,
    val following: Int?,
    val blog: String?
)