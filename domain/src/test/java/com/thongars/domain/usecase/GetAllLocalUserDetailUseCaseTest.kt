package com.thongars.domain.usecase

import com.thongars.data.database.model.UserDetailEntity
import com.thongars.data.database.model.UserEntity
import com.thongars.domain.mapper.toDomain
import com.thongars.domain.repository.LocalUserRepository
import com.thongars.utilities.test.BaseTestClassNoPowerMock
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import org.junit.Assert
import org.junit.Test

class GetAllLocalUserDetailUseCaseTest: BaseTestClassNoPowerMock() {

    private lateinit var target: GetAllLocalUserDetailUseCase

    private val localUserRepository: LocalUserRepository = mockk()

    override fun setUp() {
        super.setUp()
        target = GetAllLocalUserDetailUseCase(
            localUserRepository,
        )
    }

    private val mockUserDetailEntities = listOf(
        UserDetailEntity(
            login = "user1",
            location = "New York",
            followers = 150,
            following = 200,
            blog = "https://user1blog.com"
        ),
        UserDetailEntity(
            login = "user2",
            location = "San Francisco",
            followers = 300,
            following = 100,
            blog = null
        )
    )

    private val userEntity1 = UserEntity(
        login = "user1",
        htmlUrl = "html1",
        avatarUrl = "Avatar1",
        order = 1
    )

    private val userEntity2 = UserEntity(
        login = "user2",
        htmlUrl = "html2",
        avatarUrl = "Avatar2",
        order = 2
    )

    @Test
    fun `test get all user detail success`() = mainCoroutineRule.runBlockingTest {
        every {
            localUserRepository.getAllUserDetail()
        } returns flowOf(
            mockUserDetailEntities
        )

        every {
            localUserRepository.getUser(
                mockUserDetailEntities[0].login
            )
        } returns userEntity1

        every {
            localUserRepository.getUser(
                mockUserDetailEntities[1].login
            )
        } returns userEntity2

        val actual = target.invoke().toList()

        Assert.assertEquals(
            actual.first()?.size,
            2
        )

        Assert.assertEquals(
            actual.first()?.get(0),
            mockUserDetailEntities[0].toDomain(userEntity1)
        )

        Assert.assertEquals(
            actual.first()?.get(1),
            mockUserDetailEntities[1].toDomain(userEntity2)
        )

    }

}