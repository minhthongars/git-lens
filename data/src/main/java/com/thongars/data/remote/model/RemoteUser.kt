package com.thongars.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteUser(
    val id: Int? = null,
    val login: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("html_url")
    val htmlUrl: String? = null,
)