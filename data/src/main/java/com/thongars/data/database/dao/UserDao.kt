package com.thongars.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.thongars.data.database.DatabaseConstant
import com.thongars.data.database.model.UserDetailEntity
import com.thongars.data.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM ${DatabaseConstant.USER_TABLE_NAME}")
    fun pagingSource(): PagingSource<Int, UserEntity>

    @Upsert
    suspend fun upsertAll(users: List<UserEntity>)

    @Query("DELETE FROM ${DatabaseConstant.USER_TABLE_NAME}")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) > 0 FROM ${DatabaseConstant.USER_TABLE_NAME}")
    suspend fun hasData(): Boolean

    @Query("Select timestamp From ${DatabaseConstant.USER_TABLE_NAME} Order By timestamp DESC LIMIT 1")
    suspend fun getInsertionTime(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDetail(userDetail: UserDetailEntity)

    @Query("SELECT * FROM ${DatabaseConstant.USER_DETAIL_TABLE_NAME} WHERE login = :username")
    fun getUserDetail(username: String): UserDetailEntity?

    @Query("SELECT * FROM ${DatabaseConstant.USER_TABLE_NAME} WHERE login = :username")
    fun getUser(username: String): UserEntity?

    @Query("SELECT * FROM ${DatabaseConstant.USER_DETAIL_TABLE_NAME}")
    fun getAllUserDetail(): Flow<List<UserDetailEntity>>
}