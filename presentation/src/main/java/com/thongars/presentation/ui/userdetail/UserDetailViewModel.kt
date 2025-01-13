package com.thongars.presentation.ui.userdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thongars.domain.DefaultDispatcher
import com.thongars.domain.model.UserDetail
import com.thongars.domain.usecase.GetAllLocalUserDetailUseCase
import com.thongars.domain.usecase.GetUserDetailUseCase
import com.thongars.domain.usecase.SaveUserDetailUseCase
import com.thongars.presentation.mapper.toPresentation
import com.thongars.presentation.ui.route.Screen
import com.thongars.utilities.handleResourceState
import com.thongars.utilities.mapToAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thongars.presentation.model.UserDetail as UiUserDetail

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val saveUserDetailUseCase: SaveUserDetailUseCase,
    private val getAllLocalUserDetailUseCase: GetAllLocalUserDetailUseCase,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Data passed through the navigation component from the user listing screen
    private val userCommonData = Screen.UserDetail.from(savedStateHandle).user

    // Placeholder data passed from the user listing screen, The remaining data is waiting for API response to display more.
    private val loadingData = UiUserDetail.fake().copy(
        user = userCommonData.toPresentation()
    )

    private val _uiState: MutableStateFlow<UiState?> = MutableStateFlow(null)
    val uiState: StateFlow<UiState?> = _uiState.asStateFlow()

    init {
        observeLocalChange() // Observe changes in local database entries
        loadData()           // Load data from both local and remote sources
    }

    private fun observeLocalChange() {
        getAllLocalUserDetailUseCase
            .invoke() // Automatically triggered when a user detail is added to the database
            .map { userDetailList ->
                // Find the current user's details from the list of user details.
                // (Details are saved only when viewing user details)
                val userDetail = userDetailList?.find {
                    it.user.login == userCommonData.login
                }
                if (userDetail != null) {
                    _uiState.update {
                        UiState.Success(
                            data = userDetail.toPresentation()
                        )
                    }
                }
            }
            .catch { e ->
                // Set the error state if an exception occurs while fetching data
                setErrorState(e.mapToAppError().errorMessage)
            }
            .flowOn(dispatcher)
            .launchIn(viewModelScope)
    }

    private fun isLocalDataShowing(): Boolean {
        val data = (uiState.value as? UiState.Success)?.data ?: return false
        return data != loadingData // Check if the currently displayed data is not the placeholder data
    }

    private fun setErrorState(message: String) {
        // Show the error state only if the API call fails AND there's no
        // data in the local database for the user
        if (isLocalDataShowing().not()) {
            _uiState.update {
                UiState.Error(message)
            }
        }
    }

    fun loadData() {
        showCommonDataFirst() // Show partial data from the user listing screen
        fetchRemoteUserData() // Fetch the full data from the remote server
    }

    private fun showCommonDataFirst() {
        _uiState.update {
            UiState.Success(
                data = loadingData
            )
        }
    }

    private fun fetchRemoteUserData() {
        viewModelScope.launch(dispatcher) {
            getUserDetailUseCase
                .invoke(
                    username = userCommonData.login,
                    dispatcher = dispatcher
                )
                .collect { state ->
                    handleResourceState(
                        resourceState = state,
                        onSuccess = { data ->
                            // Save the remote data to the database to trigger updates
                            upsertUserDetailFromRemote(data)
                        },
                        onError = { e ->
                            // Handle errors by setting the error state
                            setErrorState(e)
                        }
                    )
                }
        }
    }

    private suspend fun upsertUserDetailFromRemote(user: UserDetail) {
        saveUserDetailUseCase.invoke(
            user = user,
            dispatcher = dispatcher
        ).collect { state ->
            handleResourceState(
                resourceState = state,
                onError = { e ->
                    // Handle errors while saving data to the database
                    setErrorState(e)
                }
            )
        }
    }

    sealed class UiState {
        data class Success(val data: UiUserDetail) : UiState()
        data class Error(val message: String) : UiState()
    }
}
