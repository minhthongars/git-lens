package com.thongars.data.database.di

import android.content.Context
import androidx.room.Room
import com.thongars.data.database.DatabaseConstant
import com.thongars.data.database.LocalUserRepositoryImpl
import com.thongars.data.database.UserDatabase
import com.thongars.data.database.dao.UserDao
import com.thongars.domain.repository.LocalUserRepository
import com.thongars.utilities.DispatcherName
import org.koin.core.qualifier.named
import org.koin.dsl.module


val databaseModule = module {
    single<UserDatabase> {
        Room.databaseBuilder(
            get<Context>(),
            UserDatabase::class.java,
            DatabaseConstant.DATABASE_NAME
        ).build()
    }

    single<UserDao> {
        get<UserDatabase>().dao
    }

    single<LocalUserRepository> {
        LocalUserRepositoryImpl(
            dao = get(),
            pager = get(),
            ioDispatcher = get(named(DispatcherName.IO))
        )
    }

}