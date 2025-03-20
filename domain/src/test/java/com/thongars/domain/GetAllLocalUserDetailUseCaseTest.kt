package com.thongars.domain

import com.thongars.domain.model.User
import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.LocalUserRepository
import com.thongars.domain.usecase.GetAllLocalUserDetailUseCase
import com.thongars.test.BaseTestClassNoPowerMock
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class GetAllLocalUserDetailUseCaseTest: BaseTestClassNoPowerMock() {

    private val localUserRepository: LocalUserRepository = mockk()
    private val target = GetAllLocalUserDetailUseCase(localUserRepository)

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
    fun `get all local user detail return empty`() = runTest {

        every {
            localUserRepository.getAllUserDetail()
        } returns flowOf(emptyList())

        val actualResult = target.invoke().toList()

        Assert.assertTrue(actualResult.size == 1)
        Assert.assertTrue(actualResult.first().isEmpty())
    }

    @Test
    fun `get all local user detail return data`() = runTest {

        val mockData = listOf(mockUserDetail)
        every {
            localUserRepository.getAllUserDetail()
        } returns flowOf(mockData)

        val actualResult = target.invoke().toList()

        Assert.assertTrue(actualResult.size == 1)
        Assert.assertTrue(actualResult.first() == mockData)
    }


}