package com.thongars.data.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.thongars.data.database.DatabaseConstant

@Entity(
    tableName = DatabaseConstant.USER_DETAIL_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [DatabaseConstant.LOGIN_FOREIGN_KEY],
            childColumns = [DatabaseConstant.LOGIN_FOREIGN_KEY],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [DatabaseConstant.LOGIN_FOREIGN_KEY])]
)
data class UserDetailEntity(
    @PrimaryKey
    val login: String,
    val location: String?,
    val followers: Int?,
    val following: Int?,
    val blog: String?
)