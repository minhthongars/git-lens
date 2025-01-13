package com.thongars.domain.usecase

import android.database.sqlite.SQLiteException
import com.thongars.data.remote.model.RemoteUserDetail
import com.thongars.domain.mapper.toDomain
import com.thongars.domain.model.User
import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.LocalUserRepository
import com.thongars.utilities.AppError
import com.thongars.utilities.ResourceState
import com.thongars.utilities.test.BaseTestClassNoPowerMock
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import org.junit.Assert
import org.junit.Test
import java.net.SocketTimeoutException

class SaveUserDetailUseCaseTest: BaseTestClassNoPowerMock() {

    private lateinit var target: SaveUserDetailUseCase

    private val localUserRepository: LocalUserRepository = mockk()

    private val mockedUsername = "minhthong"

    private val mockedUserDetail = UserDetail(
        user = User(
            login = "minhthong",
            avatarUrl = "https://example.com/avatar1.png",
            htmlUrl = "https://example.com/remoteUser1",
        ),
        location = "New York",
        followers = 1000,
        following = 500,
        blog = "https://blog1.com"
    )

    override fun setUp() {
        super.setUp()
        target = SaveUserDetailUseCase(localUserRepository)
    }

    @Test
    fun `test save user success`() = mainCoroutineRule.runBlockingTest {

        coEvery {
            localUserRepository.insertUserDetail(mockedUserDetail)
        } returns Unit

        val invoke = target.invoke(mockedUserDetail, testCoroutineDispatcher)

        val actual = invoke.toList()

        Assert.assertEquals(
            Unit,
            (actual.last() as ResourceState.Success).data
        )

        Assert.assertTrue(
            actual.first() is ResourceState.Loading
        )
    }

    @Test
    fun `test save user failed`() = mainCoroutineRule.runBlockingTest {

        coEvery {
            localUserRepository.insertUserDetail(mockedUserDetail)
        } throws SQLiteException()

        val invoke = target.invoke(mockedUserDetail, testCoroutineDispatcher)

        val actual = invoke.toList()

        Assert.assertEquals(
            AppError.DatabaseError().errorMessage,
            (actual.last() as ResourceState.Error).message
        )

        Assert.assertTrue(
            actual.first() is ResourceState.Loading
        )
    }

}