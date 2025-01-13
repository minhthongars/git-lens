package com.thongars.domain.usecase

import com.thongars.data.remote.model.RemoteUserDetail
import com.thongars.domain.mapper.toDomain
import com.thongars.domain.repository.RemoteUserRepository
import com.thongars.utilities.AppError
import com.thongars.utilities.ResourceState
import com.thongars.utilities.test.BaseTestClassNoPowerMock
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import org.junit.Assert
import org.junit.Test
import java.net.SocketTimeoutException

class GetUserDetailUseCaseTest: BaseTestClassNoPowerMock() {

    private lateinit var target: GetUserDetailUseCase

    private val remoteUserRepository: RemoteUserRepository = mockk()

    private val mockedUsername = "minhthong"

    private val mockedRemoteUserDetail = RemoteUserDetail(
        login = "minhthong",
        avatarUrl = "https://example.com/avatar1.png",
        htmlUrl = "https://example.com/remoteUser1",
        location = "New York",
        followers = 1000,
        following = 500,
        blog = "https://blog1.com"
    )

    override fun setUp() {
        super.setUp()
        target = GetUserDetailUseCase(remoteUserRepository)
    }

    @Test
    fun `test get user detail success`() = mainCoroutineRule.runBlockingTest {

        coEvery {
            remoteUserRepository.getUserDetail(mockedUsername)
        } returns mockedRemoteUserDetail

        val invoke = target.invoke(mockedUsername, testCoroutineDispatcher)

        val actual = invoke.toList()

        Assert.assertEquals(
            mockedRemoteUserDetail.toDomain(),
            (actual.last() as ResourceState.Success).data
        )

        Assert.assertTrue(
            actual.first() is ResourceState.Loading
        )
    }

    @Test
    fun `test get user detail failed`() = mainCoroutineRule.runBlockingTest {

        coEvery {
            remoteUserRepository.getUserDetail(mockedUsername)
        } throws SocketTimeoutException()

        val invoke = target.invoke(mockedUsername, testCoroutineDispatcher)

        val actual = invoke.toList()

        Assert.assertEquals(
            AppError.TimeoutError().errorMessage,
            (actual.last() as ResourceState.Error).message
        )

        Assert.assertTrue(
            actual.first() is ResourceState.Loading
        )
    }

}