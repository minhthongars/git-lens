package com.thongars.presentation

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import com.thongars.domain.usecase.FetchUserUseCase

import com.thongars.presentation.mapper.toPresentation
import com.thongars.presentation.model.User
import com.thongars.presentation.ui.userlisting.UserListingViewModel
import com.thongars.utilities.test.BaseTestClassNoPowerMock
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert
import org.junit.Test
import java.net.SocketTimeoutException

class UserListingViewModelTest: BaseTestClassNoPowerMock() {

    private lateinit var target: UserListingViewModel
    private val fetchUserUseCase: FetchUserUseCase = mockk(relaxed = true, relaxUnitFun = true)

    private val loadStates =  LoadStates(
        append = LoadState.NotLoading(true),
        refresh = LoadState.NotLoading(true),
        prepend = LoadState.NotLoading(false)
    )

    private val mockedUiUser = User(
        userName = "minhthong",
        landingPageUrl = "langding.vn",
        avatarUrl = "avatar.vn"
    )

    override fun setUp() {
        super.setUp()

        target = UserListingViewModel(
            fetchUserUseCase,
            testCoroutineDispatcher
        )
    }

    @Test
    fun `test ui state`() = mainCoroutineRule.runBlockingTest {

        val uiStates = mutableListOf<UserListingViewModel.UiState?>()
        val job = launch(testCoroutineDispatcher) {
            target.uiState.collect {
                uiStates.add(it)
            }
        }

        target.setCombinedLoadStates(
            CombinedLoadStates(
                refresh = LoadState.Loading,
                append = LoadState.Loading,
                prepend = LoadState.Loading,
                source = loadStates,
                mediator = loadStates
            )
        )

        delay(100)

        target.setCombinedLoadStates(
            CombinedLoadStates(
                refresh = LoadState.Loading,
                append = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.Loading,
                source = loadStates,
                mediator = loadStates
            )
        )

        delay(100)

        target.setCombinedLoadStates(
            CombinedLoadStates(
                refresh = LoadState.Error(SocketTimeoutException()),
                append = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.Loading,
                source = loadStates,
                mediator = loadStates
            )
        )

        delay(100)

        target.setCombinedLoadStates(
            CombinedLoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                append = LoadState.Loading,
                prepend = LoadState.Loading,
                source = loadStates,
                mediator = loadStates
            )
        )

        delay(100)

        target.setCombinedLoadStates(
            CombinedLoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                append = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.Loading,
                source = loadStates,
                mediator = loadStates
            )
        )

        advanceUntilIdle()
        runCurrent()
        delay(500)

        job.cancel()

        val theFirstStateWillBeLoading = UserListingViewModel.UiState.Loading == uiStates.first() //init state
        Assert.assertTrue(theFirstStateWillBeLoading)

        val nextTwoStateStillHaveNoData = (UserListingViewModel.UiState.Loading == uiStates[1]) && (uiStates[1] == uiStates[2]) // still loading
        Assert.assertTrue(nextTwoStateStillHaveNoData)

        val nextStateIsError = uiStates[3] is UserListingViewModel.UiState.Error
        Assert.assertTrue(nextStateIsError)

        val nextState = uiStates[4] as? UserListingViewModel.UiState.Success //3rd state
        val nextStateIsSuccess = nextState != null
        val andFooterStateIsLoadingMore = nextState?.footerState is UserListingViewModel.UiState.FooterState.LoadingMore //endOfPaginationReached = false
        Assert.assertTrue(nextStateIsSuccess && andFooterStateIsLoadingMore)

        val lastState = uiStates[5] as? UserListingViewModel.UiState.Success
        val lastStateIsSuccess = lastState != null
        val andFooterStateIsEnding = lastState?.footerState is UserListingViewModel.UiState.FooterState.Ending //endOfPaginationReached = true
        Assert.assertTrue(lastStateIsSuccess && andFooterStateIsEnding)
    }

    @Test
    fun `test ui action`() = mainCoroutineRule.runBlockingTest {

        val uiActions = mutableListOf<UserListingViewModel.UiAction?>()
        val job = launch(testCoroutineDispatcher) {
            target.uiAction.collect {
                uiActions.add(it)
            }
        }

        target.reLoadUserListing()
        delay(100)
        target.navigateToUserDetailScreen(mockedUiUser)

        advanceUntilIdle()
        runCurrent()
        delay(500)
        job.cancel()

        Assert.assertTrue(
            uiActions.first() is UserListingViewModel.UiAction.ReloadUserListing
        )

        Assert.assertTrue(
            uiActions[1] is UserListingViewModel.UiAction.OpenUserDetail &&
                    (uiActions[1] as UserListingViewModel.UiAction.OpenUserDetail).data.user.toPresentation() == mockedUiUser
        )

    }

}