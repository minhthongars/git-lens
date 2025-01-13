package com.thongars.domain.repository

import com.thongars.data.database.dao.UserDao
import com.thongars.data.database.model.UserDetailEntity
import com.thongars.data.database.model.UserEntity
import com.thongars.domain.mapper.toEntity
import com.thongars.domain.model.UserDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalUserRepository @Inject constructor(
    private val dao: UserDao
) {

    suspend fun insertAndUpdateUserListing(users: List<UserEntity>) {
        dao.upsertAll(users)
    }

    suspend fun insertUserDetail(user: UserDetail) {
        dao.insertUserDetail(user.toEntity())
    }

    fun getUserDetail(username: String): UserDetailEntity? {
        return dao.getUserDetail(username)
    }

    fun getUser(username: String): UserEntity? {
        return dao.getUser(username)
    }

    fun getAllUserDetail(): Flow<List<UserDetailEntity>> {
        return dao.getAllUserDetail()
    }

    suspend fun getLastInsertionTime(): Long {
        return dao.getInsertionTime() ?: 0
    }

    suspend fun hasUserData(): Boolean {
        return dao.hasData()
    }

    suspend fun clearAllUser() {
        dao.clearAll()
    }

}