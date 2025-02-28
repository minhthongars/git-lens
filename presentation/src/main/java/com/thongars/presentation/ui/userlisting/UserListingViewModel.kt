package com.thongars.presentation.ui.userlisting

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.cachedIn
import androidx.paging.map
import com.thongars.domain.DefaultDispatcher
import com.thongars.domain.model.User
import com.thongars.domain.usecase.FetchUserUseCase
import com.thongars.presentation.R
import com.thongars.presentation.mapper.toPresentation
import com.thongars.presentation.ui.route.Screen
import com.thongars.utilities.mapToAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thongars.presentation.model.User as UiUser

@HiltViewModel
class UserListingViewModel @Inject constructor(
    fetchUserUseCase: FetchUserUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
): ViewModel() {

    private val _uiAction: Channel<UiAction?> = Channel()
    val uiAction: Flow<UiAction?> = _uiAction.receiveAsFlow()

    private val combineLoadState: MutableStateFlow<CombinedLoadStates?> = MutableStateFlow(null)

    val userPagingFlow = fetchUserUseCase
        .invoke()
        .map { paging ->
            paging.map { it.toPresentation() }
        }
        .flowOn(defaultDispatcher)
        .cachedIn(viewModelScope)

    val uiState = combineLoadState.map { loadState ->
        if (loadState == null) {
            UiState.Loading
        } else {
            handleUiState(loadState)
        }
    }.flowOn(defaultDispatcher)

    /**
    * Determine UI state based on the load state:
    * - **Loading State**: refresh is the first state when the Pager loads data,
    *          so if Refresh is in Loading state, it means the screen is in loading state,
    *          when appending data and have an error, the user presses refresh() --> Append is Error && Refresh is Loading will happen,
    *          but I don't want the screen to be in Error state, it will be Success state and footer state error, that's why I put the condition "!is"
    *          (because when the pager reaches Append state, it means that there is data displayed on the screen, so I don't want the screen to have an error state)
     *
    * - **Error State**: Same as above, the first state is Error && no data displayed on the screen
     *
    * - **Success State**: not the above 2 states, it means that the Append data state has been reached
    */
    private fun handleUiState(loadState: CombinedLoadStates): UiState {

        val isLoadingState = loadState.refresh is LoadState.Loading &&
                loadState.append !is LoadState.Error
        val isErrorState = loadState.refresh is LoadState.Error &&
                loadState.append !is LoadState.Error
        val isSuccessState = isLoadingState.not() && isErrorState.not()

        return when {
            isLoadingState -> {
                UiState.Loading
            }
            isErrorState -> {
                val errorMessage = (loadState.refresh as LoadState.Error)
                    .error
                    .mapToAppError()
                    .errorMessage
                UiState.Error(errorMessage)
            }
            isSuccessState -> {
                UiState.Success(
                    footerState = getFooterState(loadState)
                )

            }
            else -> UiState.Error("An unknown error occurred")
        }
    }

    /**
     * This is the state to determine the UI at the END (BOTTOM) of the list
     * - **End of Pagination**: You have seen the end of the list, Append cannot load more and endOfPaginationReached = true,
     *          you can hard code the api to return a list with a size smaller than 1 page to see it.
     * - **Loading More**: That means appending more data --> you can delay the api call and scroll really fast to see it
     * - **Append Error**: Append data when there is an error --> you can hard code the api to have an error WHEN LOADING MORE data to see it
     */
    private fun getFooterState(loadState: CombinedLoadStates): UiState.FooterState {
        val isEndOfPagination =  loadState.append is LoadState.NotLoading &&
                loadState.append.endOfPaginationReached
        val isLoadingMore = loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading
        val isErrorHappen = loadState.append is LoadState.Error

        return when {
            isLoadingMore -> UiState.FooterState.LoadingMore
            isEndOfPagination -> UiState.FooterState.Ending(R.string.end_of_list)
            isErrorHappen -> {
                val errorMessage = (loadState.append as LoadState.Error)
                    .error
                    .mapToAppError()
                    .errorMessage
                UiState.FooterState.Error(errorMessage)
            }
            else -> UiState.FooterState.LoadingMore
        }
    }

    fun setCombinedLoadStates(loadStates: CombinedLoadStates) {
        viewModelScope.launch(defaultDispatcher) {
            combineLoadState.update { loadStates }
        }
    }

    fun navigateToUserDetailScreen(user: UiUser) {

        val userCommonData = User(
            login = user.userName,
            avatarUrl = user.avatarUrl,
            htmlUrl = user.landingPageUrl,
            order = -1
        )
        sendUiAction(
            UiAction.OpenUserDetail(
                Screen.UserDetail(
                    userCommonData
                )
            )
        )
    }

    fun reLoadUserListing() {
        sendUiAction(UiAction.ReloadUserListing)
    }

    private fun sendUiAction(action: UiAction) {
        viewModelScope.launch(defaultDispatcher) {
            _uiAction.trySend(action)
        }
    }

    sealed class UiState {

        sealed class FooterState {
            data object LoadingMore: FooterState()
            class Error(val message: String): FooterState()
            class Ending(@StringRes val message: Int): FooterState()
        }

        data object Loading: UiState()
        class Error(val message: String): UiState()
        class Success(
            val footerState: FooterState
        ): UiState()
    }

    sealed class UiAction {

        data object ReloadUserListing: UiAction()
        class OpenUserDetail(val data: Screen.UserDetail): UiAction()
    }
}