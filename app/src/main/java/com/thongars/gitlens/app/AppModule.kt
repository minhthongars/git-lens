package com.thongars.gitlens.app

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.thongars.data.UserMediator
import com.thongars.data.database.dao.UserDao
import com.thongars.data.database.model.UserEntity
import com.thongars.data.remote.ApiConstant
import com.thongars.data.remote.GitHubApi
import com.thongars.domain.model.User
import com.thongars.domain.repository.LocalUserRepository
import com.thongars.domain.repository.RemoteUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @Singleton
    fun provideUserPager(
        userDao: UserDao,
        gitHubApi: GitHubApi,
    ): Pager<Int, UserEntity> {
        return Pager(
            config = PagingConfig(
                pageSize = ApiConstant.FETCH_USER_LIMIT,
                initialLoadSize = ApiConstant.FETCH_USER_LIMIT,
                prefetchDistance = ApiConstant.FETCH_USER_LIMIT
            ),
            remoteMediator = UserMediator(
                gitHubApi = gitHubApi,
                userDao = userDao
            ),
            pagingSourceFactory = {
                userDao.pagingSource()
            }
        )
    }
}