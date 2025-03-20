package com.thongars.domain

import com.thongars.domain.model.User
import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.LocalUserRepository
import com.thongars.domain.usecase.SaveUserDetailUseCase
import com.thongars.utilities.ResourceState
import com.thongars.test.BaseTestClassNoPowerMock
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class SaveUserDetailUseCaseTest: BaseTestClassNoPowerMock() {

    private val localUserRepository: LocalUserRepository = mockk()
    private val target = SaveUserDetailUseCase(localUserRepository)

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
    fun `save user detail return result`() = runTest {

        val mockData = ResourceState.Success(Unit)
        coEvery {
            localUserRepository.insertUserDetail(mockUserDetail)
        } returns flowOf(mockData)

        val actualResult = target.invoke(mockUserDetail).toList()

        Assert.assertTrue(actualResult.size == 1)
        Assert.assertTrue(actualResult.first() == mockData)
    }


}