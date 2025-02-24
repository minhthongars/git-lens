package com.thongars.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val order: Int,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String
)