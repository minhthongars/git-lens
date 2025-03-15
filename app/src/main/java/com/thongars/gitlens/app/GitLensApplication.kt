package com.thongars.gitlens.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.thongars.data.database.di.databaseModule
import com.thongars.data.remote.di.remoteModule
import com.thongars.data.coroutineDispatchersModule
import com.thongars.domain.domainModule
import com.thongars.presentation.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class GitLensApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader() = ImageLoader.Builder(this)
        .crossfade(true)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(cacheDir.resolve("image_cache"))
                .maxSizePercent(0.04)
                .build()
        }
        .build()

    override fun onCreate() {
        super.onCreate()


        startKoin {
            androidContext(this@GitLensApplication)
            modules(
                listOf(
                    databaseModule,
                    remoteModule,
                    appModule,
                    domainModule,
                    presentationModule,
                    coroutineDispatchersModule
                )
            )
        }
    }
}