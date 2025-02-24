package com.thongars.data.remote.di

import android.util.Log
import com.thongars.data.BuildConfig
import com.thongars.data.remote.ApiConstant
import com.thongars.data.remote.GitHubApi
import com.thongars.data.remote.RemoteUserRepositoryImpl
import com.thongars.domain.IoDispatcher
import com.thongars.domain.repository.RemoteUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideGitHubApi(): GitHubApi {

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(ApiConstant.CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(ApiConstant.READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(ApiConstant.WRITE_TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.API_KEY}")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
        return Retrofit.Builder()
            .baseUrl(ApiConstant.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GitHubApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteRepository(
        api: GitHubApi,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): RemoteUserRepository {
        return RemoteUserRepositoryImpl(api, dispatcher)
    }

}