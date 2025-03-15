package com.thongars.domain

import com.thongars.domain.usecase.FetchUserUseCase
import com.thongars.domain.usecase.GetAllLocalUserDetailUseCase
import com.thongars.domain.usecase.GetUserDetailUseCase
import com.thongars.domain.usecase.SaveUserDetailUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { FetchUserUseCase(get()) }
    factory { GetAllLocalUserDetailUseCase(get()) }
    factory { GetUserDetailUseCase(get()) }
    factory { SaveUserDetailUseCase(get()) }
}