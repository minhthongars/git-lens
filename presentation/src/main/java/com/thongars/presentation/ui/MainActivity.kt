package com.thongars.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Keep
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thongars.presentation.R
import com.thongars.presentation.ui.route.Screen
import com.thongars.presentation.ui.theme.GitLensTheme
import com.thongars.presentation.ui.userdetail.UserDetailScreen
import com.thongars.presentation.ui.userlisting.UserListingScreen
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitLensTheme(
                dynamicColor = false
            ) {
                MainScreen()
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel = koinViewModel()) {
    val navController = rememberNavController()

    val currentBackStack by navController
        .currentBackStackEntryFlow
        .collectAsStateWithLifecycle(
            initialValue = null
        )

    mainViewModel.setCurrentRoute(currentBackStack?.destination?.route)

    val screenTitle by mainViewModel
        .screenTitle
        .collectAsStateWithLifecycle(
            initialValue = R.string.user_list
        )
    val isShowBackIcon by mainViewModel
        .isShowBackIcon
        .collectAsStateWithLifecycle(
            initialValue = false
        )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = screenTitle)) },
                navigationIcon = {
                    if (isShowBackIcon) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.UserListing,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Screen.UserListing> {
                UserListingScreen(
                    navController = navController
                )
            }
            composable<Screen.UserDetail>(
                typeMap = Screen.UserDetail.typeMap
            ) {
                UserDetailScreen()
            }
        }
    }
}


