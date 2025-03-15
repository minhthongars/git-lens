package com.thongars.gitlens.app

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.thongars.data.UserMediator
import com.thongars.data.database.dao.UserDao
import com.thongars.data.database.model.UserEntity
import com.thongars.data.remote.ApiConstant
import org.koin.dsl.module

@OptIn(ExperimentalPagingApi::class)
val appModule = module {
    single<Pager<Int, UserEntity>> {
        Pager(
            config = PagingConfig(
                pageSize = ApiConstant.FETCH_USER_LIMIT,
                initialLoadSize = ApiConstant.FETCH_USER_LIMIT,
                prefetchDistance = ApiConstant.FETCH_USER_LIMIT
            ),
            remoteMediator = UserMediator(
                gitHubApi = get(),
                userDao = get()
            ),
            pagingSourceFactory = {
                get<UserDao>().pagingSource()
            }
        )
    }
}