package com.thongars.data.remote.di

import com.thongars.data.BuildConfig
import com.thongars.data.remote.ApiConstant
import com.thongars.data.remote.GitHubApi
import com.thongars.data.remote.RemoteUserRepositoryImpl
import com.thongars.domain.repository.RemoteUserRepository
import com.thongars.utilities.DispatcherName
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


val remoteModule = module {

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .connectTimeout(ApiConstant.CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(ApiConstant.READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(ApiConstant.WRITE_TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader(ApiConstant.AUTHORIZATION, "${ApiConstant.BEARER} ${BuildConfig.API_KEY}")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    single<GitHubApi> {
        Retrofit.Builder()
            .baseUrl(ApiConstant.BASE_URL)
            .client(
                get<OkHttpClient>()
            )
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GitHubApi::class.java)
    }

    single<RemoteUserRepository> {
        RemoteUserRepositoryImpl(
            get(),
            get(named(DispatcherName.IO))
        )
    }
}
