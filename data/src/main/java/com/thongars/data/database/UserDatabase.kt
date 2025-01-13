package com.thongars.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thongars.data.database.dao.UserDao
import com.thongars.data.database.model.UserDetailEntity
import com.thongars.data.database.model.UserEntity

@Database(
    entities = [UserEntity::class, UserDetailEntity::class],
    version = DatabaseConstant.DATABASE_VERSION
)
abstract class UserDatabase: RoomDatabase() {

    abstract val dao: UserDao
}