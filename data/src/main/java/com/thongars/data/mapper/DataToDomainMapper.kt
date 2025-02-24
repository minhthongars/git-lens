package com.thongars.data.mapper

import com.thongars.data.database.model.UserDetailEntity
import com.thongars.data.database.model.UserEntity
import com.thongars.data.remote.model.RemoteUser
import com.thongars.data.remote.model.RemoteUserDetail
import com.thongars.domain.model.User
import com.thongars.domain.model.UserDetail

fun UserEntity.toDomain() = User(
    login = login,
    avatarUrl = avatarUrl.orEmpty(),
    htmlUrl = htmlUrl.orEmpty(),
    order = order
)

fun RemoteUser.toDomain() = User(
    login = login.orEmpty(),
    avatarUrl = avatarUrl.orEmpty(),
    htmlUrl = htmlUrl.orEmpty(),
    order = id ?: -0
)

fun RemoteUser.toEntity() = UserEntity(
    login = login.orEmpty(),
    avatarUrl = avatarUrl.orEmpty(),
    htmlUrl = htmlUrl.orEmpty(),
    order = id ?: 0
)

fun UserDetailEntity.toDomain(userEntity: UserEntity) = UserDetail(
    user = userEntity.toDomain(),
    followers = followers ?: -1,
    following = following ?: -1,
    location = location.orEmpty(),
    blog = blog.orEmpty()
)

fun UserDetailEntity.toDomain(user: User) = UserDetail(
    user = user,
    followers = followers ?: -1,
    following = following ?: -1,
    location = location.orEmpty(),
    blog = blog.orEmpty()
)

fun RemoteUserDetail.toDomain() = UserDetail(
    user = User(
        login = login.orEmpty(),
        avatarUrl = avatarUrl.orEmpty(),
        htmlUrl = htmlUrl.orEmpty(),
        order = id ?: -1
    ),
    followers = followers ?: -1,
    following = following ?: -1,
    location = location.orEmpty(),
    blog = blog.orEmpty()
)

fun UserDetail.toEntity() = UserDetailEntity(
    login = user.login,
    following = following,
    followers = followers,
    location = location,
    blog = blog
)

fun User.toEntity() = UserEntity(
    login = login,
    avatarUrl = avatarUrl,
    htmlUrl = htmlUrl,
    order = order
)