package com.thongars.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String
)