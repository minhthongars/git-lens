package com.thongars.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thongars.data.database.DatabaseConstant

@Entity(tableName = DatabaseConstant.USER_TABLE_NAME)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = DatabaseConstant.LOGIN_FOREIGN_KEY)
    val login: String,
    val order: Int,
    val avatarUrl: String?,
    val htmlUrl: String?,
    val timestamp: Long? = System.currentTimeMillis(),
)