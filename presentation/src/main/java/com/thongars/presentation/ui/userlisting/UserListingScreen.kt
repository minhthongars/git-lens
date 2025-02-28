package com.thongars.presentation.ui.userlisting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.thongars.presentation.R
import com.thongars.presentation.model.User
import com.thongars.presentation.ui.component.ErrorScreenComponent
import com.thongars.presentation.ui.component.FooterMessage
import com.thongars.presentation.ui.component.LoadingScreenComponent
import com.thongars.presentation.ui.component.UserItem
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun UserListingScreen(
    viewModel: UserListingViewModel = hiltViewModel(),
    navController: NavHostController
) {

    val userPagingItems = viewModel.userPagingFlow.collectAsLazyPagingItems()
    viewModel.setCombinedLoadStates(userPagingItems.loadState)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle(initialValue = UserListingViewModel.UiState.Loading)

    LaunchedEffect(viewModel.uiAction) {
        viewModel
            .uiAction
            .onEach { action ->
                when(action) {
                    is UserListingViewModel.UiAction.OpenUserDetail -> {
                        navController.navigate(action.data)
                    }
                    is UserListingViewModel.UiAction.ReloadUserListing -> {
                        userPagingItems.refresh()
                    }
                    else -> Unit
                }
            }
            .launchIn(this)
    }

    UserListingContent(
        uiState = uiState,
        pagingItems = userPagingItems,
        onUserClick = { user ->
            viewModel.navigateToUserDetailScreen(user)
        },
        onRetryClicked = {
            viewModel.reLoadUserListing()
        }
    )

}

@Composable
fun UserListingContent(
    uiState: UserListingViewModel.UiState,
    pagingItems: LazyPagingItems<User>,
    onUserClick: (User) -> Unit,
    onRetryClicked: () -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {
        when(uiState) {
            is UserListingViewModel.UiState.Error -> {
                ErrorScreenComponent(
                    message = uiState.message
                ) {
                    onRetryClicked()
                }
            }

            is UserListingViewModel.UiState.Loading -> {
                LoadingScreenComponent()
            }

            is UserListingViewModel.UiState.Success -> {
                UserListingDisplay(
                    state = uiState,
                    pagingItems = pagingItems,
                    onUserClick = onUserClick,
                    onRetryClicked = onRetryClicked
                )
            }
        }
    }
}

@Composable
fun UserListingDisplay(
    state: UserListingViewModel.UiState.Success,
    pagingItems: LazyPagingItems<User>,
    onUserClick: (User) -> Unit,
    onRetryClicked: () -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        items(
            count = pagingItems.itemCount,
            key = { index -> pagingItems[index]?.userName.orEmpty() }
        ) { index ->
            pagingItems[index]?.let { user ->
                UserItem(
                    user = user,
                    onUserClick = {
                        onUserClick(user)
                    }
                ) {
                    Text(
                        text = user.landingPageUrl,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }

        item {

            UserListingFooter(
                footerState = state.footerState
            ) {
                onRetryClicked()
            }
        }
    }
}

@Composable
fun UserListingFooter(
    footerState: UserListingViewModel.UiState.FooterState,
    onTryAgainClicked: () -> Unit
) {

    when(footerState) {
        is UserListingViewModel.UiState.FooterState.LoadingMore -> {
            CircularProgressIndicator()
        }

        is UserListingViewModel.UiState.FooterState.Ending -> {
            FooterMessage(stringResource(footerState.message))
        }

        is UserListingViewModel.UiState.FooterState.Error -> {
            Box(
                modifier = Modifier.clickable { onTryAgainClicked() }
            ) {
                FooterMessage(footerState.message)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserListingPreView() {

    val sampleUsers = listOf(
        User("David", "https://via.placeholder.com/150", "https://linkedin.com/"),
        User("Lisa", "https://via.placeholder.com/150", "https://linkedin.com/"),
        User("Mary Black", "https://via.placeholder.com/150", "https://linkedin.com/")
    )

    UserListingContent(
        uiState = UserListingViewModel.UiState.Success(
            footerState = UserListingViewModel.UiState.FooterState.LoadingMore
        ),
        onUserClick = {},
        onRetryClicked = {},
        pagingItems = flowOf(
            PagingData.from(
                sampleUsers,
                LoadStates(
                    append = LoadState.NotLoading(true),
                    refresh = LoadState.NotLoading(true),
                    prepend = LoadState.NotLoading(false)
                )
            )
        ).collectAsLazyPagingItems()
    )
}