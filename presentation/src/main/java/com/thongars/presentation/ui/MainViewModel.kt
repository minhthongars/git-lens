package com.thongars.presentation.ui

import androidx.fragment.app.FragmentManager.BackStackEntry
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.thongars.presentation.R
import com.thongars.presentation.ui.route.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {

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