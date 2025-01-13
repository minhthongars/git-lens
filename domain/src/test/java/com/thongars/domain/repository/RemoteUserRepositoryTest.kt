package com.thongars.domain.repository

import com.thongars.data.remote.GitHubApi
import com.thongars.data.remote.model.RemoteUser
import com.thongars.data.remote.model.RemoteUserDetail
import com.thongars.utilities.test.BaseTestClassNoPowerMock
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoteUserRepositoryTest: BaseTestClassNoPowerMock() {

    private val gitHubApi: GitHubApi = mockk()

    private lateinit var remoteUserRepository: RemoteUserRepository

    override fun setUp() {
        super.setUp()
        remoteUserRepository = RemoteUserRepository(gitHubApi)
    }

    private val mockedRemoteUsers = listOf(
        RemoteUser(
            id = 1,
            login = "login1",
            avatarUrl = "avatar1",
            htmlUrl = "html1"
        ),
        RemoteUser(
            id = 2,
            login = "login2",
            avatarUrl = "avatar2",
            htmlUrl = "html2"
        )
    )

    private val mockedRemoteUserDetail = RemoteUserDetail(
        login = "username",
        avatarUrl = "avatar",
        location = "location",
        followers = 1,
        following = 2,
        blog = "blog",
        htmlUrl = ""
    )


    @Test
    fun `fetchUserListing should return list of RemoteUser from api`() = runTest {
        val limit = 10
        val since = 0

        coEvery { gitHubApi.getUsers(limit, since) } returns mockedRemoteUsers

        val result = remoteUserRepository.fetchUserListing(limit, since)

        assertEquals(mockedRemoteUsers, result)
        coVerify { gitHubApi.getUsers(limit, since) }
    }

    @Test
    fun `getUserDetail should return RemoteUserDetail from api`() = runTest {
        val username = mockedRemoteUserDetail.login!!

        coEvery { gitHubApi.getUserDetail(username) } returns mockedRemoteUserDetail

        val result = remoteUserRepository.getUserDetail(username)

        assertEquals(mockedRemoteUserDetail, result)
        coVerify { gitHubApi.getUserDetail(username) }
    }
}
