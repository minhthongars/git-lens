package com.thongars.domain.model

data class UserDetail(
    val user: User,
    val location: String,
    val followers: Int,
    val following: Int,
    val blog: String
)