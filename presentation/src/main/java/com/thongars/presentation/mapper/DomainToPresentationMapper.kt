package com.thongars.presentation.mapper

import com.thongars.domain.model.User
import com.thongars.domain.model.UserDetail
import com.thongars.presentation.model.User as UiUser
import com.thongars.presentation.model.UserDetail as UiUserDetail

fun User.toPresentation() = UiUser(
    userName = login,
    avatarUrl = avatarUrl,
    landingPageUrl = htmlUrl
)

fun UserDetail.toPresentation() = UiUserDetail(
    user = user.toPresentation(),
    following = following.toString(),
    followers = followers.toString(),
    location = location,
    blog = blog
)