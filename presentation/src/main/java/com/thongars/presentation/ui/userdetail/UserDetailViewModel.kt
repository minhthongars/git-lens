package com.thongars.presentation.ui.userdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thongars.domain.model.UserDetail
import com.thongars.domain.usecase.GetAllLocalUserDetailUseCase
import com.thongars.domain.usecase.GetUserDetailUseCase
import com.thongars.domain.usecase.SaveUserDetailUseCase
import com.thongars.presentation.mapper.toPresentation
import com.thongars.presentation.ui.route.Screen
import com.thongars.utilities.handleResourceState
import com.thongars.utilities.mapToAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thongars.presentation.model.UserDetail as UiUserDetail

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val saveUserDetailUseCase: SaveUserDetailUseCase,
    private val getAllLocalUserDetailUseCase: GetAllLocalUserDetailUseCase,
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

        val exceptionHandle = CoroutineExceptionHandler { _, throwable ->
            setErrorState(throwable.mapToAppError().errorMessage)
        }

        viewModelScope.launch(exceptionHandle) {
            getAllLocalUserDetailUseCase
                .invoke() // Automatically triggered when a user detail is added to the database
                .filter { userDetail -> userDetail?.user?.login == userCommonData.login }
                .distinctUntilChanged()
                .collect { userDetail ->
                    _uiState.update {
                        UiState.Success(data = userDetail!!.toPresentation()) //userDetail can not null after filter
                    }
                }
        }
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
        viewModelScope.launch {
            getUserDetailUseCase
                .invoke(
                    username = userCommonData.login,
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
            user = user
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
