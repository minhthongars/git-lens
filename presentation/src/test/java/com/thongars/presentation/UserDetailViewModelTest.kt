package com.thongars.presentation

import androidx.lifecycle.SavedStateHandle
import com.thongars.domain.model.User
import com.thongars.domain.model.UserDetail
import com.thongars.presentation.model.UserDetail as UiUserDetail
import com.thongars.domain.usecase.GetAllLocalUserDetailUseCase
import com.thongars.domain.usecase.GetUserDetailUseCase
import com.thongars.domain.usecase.SaveUserDetailUseCase
import com.thongars.presentation.mapper.toPresentation
import com.thongars.presentation.ui.route.Screen
import com.thongars.presentation.ui.userdetail.UserDetailViewModel
import com.thongars.utilities.test.BaseTestClassNoPowerMock
import com.thongars.utilities.ResourceState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class UserDetailViewModelTest: BaseTestClassNoPowerMock() {

    private lateinit var target: UserDetailViewModel

    private val getUserDetailUseCase: GetUserDetailUseCase = mockk()
    private val saveUserDetailUseCase: SaveUserDetailUseCase = mockk()
    private val getAllLocalUserDetailUseCase: GetAllLocalUserDetailUseCase = mockk()
    private val dispatcher: CoroutineDispatcher = testCoroutineDispatcher
    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true, relaxUnitFun = true)

    private val mockedUser = User(
        login = "minhthong",
        avatarUrl = "avatar.com",
        htmlUrl = "html.com.vn"
    )

    private val mockedUserDetail = UserDetail(
        user = mockedUser,
        blog = "blog.com",
        location = "HCM",
        followers = 10,
        following = 12
    )

    private val mockErrorMessage = "exception"

    private val mockedUiUserDetailScreen = Screen.UserDetail(user = mockedUser)

    private val placeholderData = UiUserDetail.fake().copy(user = mockedUser.toPresentation())

    override fun setUp() {
        super.setUp()

        mockkObject(Screen.UserDetail)
        every { Screen.UserDetail.from(any()) } returns mockedUiUserDetailScreen
    }

    @Test
    fun `test uiState, no data in database, call api success and save local failed`() = runTest {

        every {
            getAllLocalUserDetailUseCase.invoke()
        } returns flowOf(listOf())

        coEvery {
            getUserDetailUseCase.invoke(mockedUser.login, testCoroutineDispatcher)
        } returns flowOf(
            ResourceState.Success(
                data = mockedUserDetail
            )
        )

        coEvery {
            saveUserDetailUseCase.invoke(mockedUserDetail, testCoroutineDispatcher)
        } returns flowOf(ResourceState.Error(message = mockErrorMessage))

        val uiStates = mutableListOf<UserDetailViewModel.UiState?>()
        val job = launch(testCoroutineDispatcher) {
            target.uiState.collect {
                uiStates.add(it)
            }
        }

        target = UserDetailViewModel(
            getUserDetailUseCase,
            saveUserDetailUseCase,
            getAllLocalUserDetailUseCase,
            dispatcher,
            savedStateHandle
        )

        advanceUntilIdle()
        runCurrent()
        delay(500)

        job.cancel()

        Assert.assertEquals(
            mockErrorMessage,
            (uiStates[1] as UserDetailViewModel.UiState.Error).message
        )

        Assert.assertEquals(
            placeholderData,
            (uiStates.first() as UserDetailViewModel.UiState.Success).data
        )
    }

    @Test
    fun `test uiState, have data in database, call api failed`() = runTest {

        every {
            getAllLocalUserDetailUseCase.invoke()
        } returns flowOf(listOf(mockedUserDetail))

        coEvery {
            getUserDetailUseCase.invoke(mockedUser.login, testCoroutineDispatcher)
        } returns flowOf(
            ResourceState.Error(
                message = mockErrorMessage
            )
        )

        val uiStates = mutableListOf<UserDetailViewModel.UiState?>()
        val job = launch(testCoroutineDispatcher) {
            target.uiState.collect {
                uiStates.add(it)
            }
        }

        target = UserDetailViewModel(
            getUserDetailUseCase,
            saveUserDetailUseCase,
            getAllLocalUserDetailUseCase,
            dispatcher,
            savedStateHandle
        )

        advanceUntilIdle()
        runCurrent()
        delay(500)

        job.cancel()

        Assert.assertEquals(
            placeholderData,
            (uiStates.first() as UserDetailViewModel.UiState.Success).data
        )

        Assert.assertEquals(
            mockedUserDetail.toPresentation(),
            (uiStates[1] as UserDetailViewModel.UiState.Success).data
        )
    }

    @Test
    fun `test uiState, have data in database, call api success, save local failed`() = runTest {

        every {
            getAllLocalUserDetailUseCase.invoke()
        } returns flowOf(listOf(mockedUserDetail))

        coEvery {
            getUserDetailUseCase.invoke(mockedUser.login, testCoroutineDispatcher)
        } returns flowOf(
            ResourceState.Success(
                data = mockedUserDetail
            )
        )

        coEvery {
            saveUserDetailUseCase.invoke(mockedUserDetail, testCoroutineDispatcher)
        } returns flowOf(ResourceState.Error(message = mockErrorMessage))

        val uiStates = mutableListOf<UserDetailViewModel.UiState?>()
        val job = launch(testCoroutineDispatcher) {
            target.uiState.collect {
                uiStates.add(it)
            }
        }

        target = UserDetailViewModel(
            getUserDetailUseCase,
            saveUserDetailUseCase,
            getAllLocalUserDetailUseCase,
            dispatcher,
            savedStateHandle
        )

        advanceUntilIdle()
        runCurrent()
        delay(500)

        job.cancel()

        Assert.assertEquals(
            placeholderData,
            (uiStates.first() as UserDetailViewModel.UiState.Success).data
        )

        Assert.assertEquals(
            mockedUserDetail.toPresentation(),
            (uiStates[1] as UserDetailViewModel.UiState.Success).data
        )
    }

}