package com.thongars.data.remote.di

import android.util.Log
import com.thongars.data.BuildConfig
import com.thongars.data.remote.ApiConstant
import com.thongars.data.remote.RemoteUserRepositoryImpl
import com.thongars.domain.repository.RemoteUserRepository
import com.thongars.utilities.DispatcherName
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module


val remoteModule = module {

    single<HttpClient> {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("HttpLogging:", message)
                    }
                }
            }

            //Http Response
            install(ResponseObserver) {
                onResponse { response ->
                    Log.d("HTTP status:", "${response.status.value}")
                }
            }

            install(DefaultRequest) {
                //header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(ApiConstant.AUTHORIZATION, "${ApiConstant.BEARER} ${BuildConfig.API_KEY}")
            }

//            engine {
//                socketTimeout = ApiConstant.SOCKET_TIMEOUT.toInt()
//                connectTimeout = ApiConstant.CONNECT_TIMEOUT.toInt()
//            }

            install(HttpTimeout) {
                requestTimeoutMillis = ApiConstant.REQUEST_TIMEOUT
                connectTimeoutMillis = ApiConstant.CONNECT_TIMEOUT
                socketTimeoutMillis = ApiConstant.SOCKET_TIMEOUT
            }
        }
    }

    factory<RemoteUserRepository> {
        RemoteUserRepositoryImpl(
            get<HttpClient>(),
            get(named(DispatcherName.IO))
        )
    }
}
