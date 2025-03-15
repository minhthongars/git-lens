package com.thongars.data

import com.thongars.utilities.DispatcherName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coroutineDispatchersModule = module {
    single<CoroutineDispatcher>(named(DispatcherName.DEFAULT)) { Dispatchers.Default }
    single<CoroutineDispatcher>(named(DispatcherName.IO)) { Dispatchers.IO }
    single<CoroutineDispatcher>(named(DispatcherName.MAIN)) { Dispatchers.Main }
}