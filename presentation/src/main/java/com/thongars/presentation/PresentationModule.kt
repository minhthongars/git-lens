package com.thongars.presentation

import com.thongars.presentation.ui.MainViewModel
import com.thongars.presentation.ui.userdetail.UserDetailViewModel
import com.thongars.presentation.ui.userlisting.UserListingViewModel
import com.thongars.utilities.DispatcherName
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val presentationModule = module {
    viewModel { UserDetailViewModel(get(), get(), get(), get()) }
    viewModel { UserListingViewModel(get(), get(named(DispatcherName.DEFAULT))) }
    viewModel { MainViewModel() }
}