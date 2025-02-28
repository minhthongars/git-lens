package com.thongars.data.database.di

import android.content.Context
import androidx.paging.Pager
import androidx.room.Room
import com.thongars.data.database.DatabaseConstant
import com.thongars.data.database.LocalUserRepositoryImpl
import com.thongars.data.database.UserDatabase
import com.thongars.data.database.dao.UserDao
import com.thongars.data.database.model.UserEntity
import com.thongars.domain.DefaultDispatcher
import com.thongars.domain.IoDispatcher
import com.thongars.domain.repository.ContentResolveRepository
import com.thongars.domain.repository.LocalUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideUserDatabase(
        @ApplicationContext context: Context
    ): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            DatabaseConstant.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(
        userDatabase: UserDatabase
    ): UserDao {
        return userDatabase.dao
    }

    @Provides
    @Singleton
    fun provideLocalRepository(
        dao: UserDao,
        pager: Pager<Int, UserEntity>,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    ): LocalUserRepository {
        return LocalUserRepositoryImpl(
            dao = dao,
            pager = pager,
            contentResolver = null,
            ioDispatcher = ioDispatcher,
            defaultDispatcher = defaultDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideContentResolverRepository(
        dao: UserDao,
        pager: Pager<Int, UserEntity>,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context,
    ): ContentResolveRepository {
        return LocalUserRepositoryImpl(
            dao = dao,
            pager = pager,
            contentResolver = context.contentResolver,
            ioDispatcher = ioDispatcher,
            defaultDispatcher = defaultDispatcher
        )
    }
}