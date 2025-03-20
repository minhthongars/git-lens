package com.thongars.domain

import com.thongars.domain.model.User
import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.RemoteUserRepository
import com.thongars.domain.usecase.GetUserDetailUseCase
import com.thongars.test.BaseTestClassNoPowerMock
import com.thongars.utilities.ResourceState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class GetUserDetailUseCaseTest: BaseTestClassNoPowerMock() {

    private val remoteUserRepository: RemoteUserRepository = mockk()
    private val target = GetUserDetailUseCase(remoteUserRepository)

    private val mockUsername = "username"
    private val mockUserDetail = UserDetail(
        user = User(
            order = 1,
            avatarUrl = "",
            htmlUrl = "",
            login = ""
        ),
        following = 1,
        followers = 2,
        blog = "",
        location = ""
    )

    @Test
    fun `get all local user detail return data`() = runTest {

        val mockData = ResourceState.Success(mockUserDetail)
        coEvery {
            remoteUserRepository.getUserDetail(mockUsername)
        } returns flowOf(mockData)

        val actualResult = target.invoke(mockUsername).toList()

        Assert.assertTrue(actualResult.size == 1)
        Assert.assertTrue(actualResult.first() == mockData)
    }

}