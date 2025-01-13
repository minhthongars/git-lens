package com.thongars.presentation.model

data class UserDetail(
    val user: User,
    val location: String,
    val followers: String,
    val following: String,
    val blog: String,
    val isLoading: Boolean = true
) {
    companion object {

        fun fake() = UserDetail(
            user = User(
                userName = "",
                avatarUrl = "",
                landingPageUrl = ""
            ),
            following = "--",
            followers = "--",
            location = "loading...",
            blog = "----"
        )
    }
}