package com.thongars.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thongars.presentation.R
import com.thongars.presentation.ui.route.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel: ViewModel() {

    private val routeState: MutableStateFlow<String> = MutableStateFlow("")

    val screenTitle = routeState.map { route ->
        Screen.screenTitleMap[route] ?: R.string.user_list
    }

    val isShowBackIcon = routeState.map { route ->
        route != Screen.UserListing.route
    }

    fun setCurrentRoute(route: String?) {
        routeState.update { route.orEmpty() }
    }

}