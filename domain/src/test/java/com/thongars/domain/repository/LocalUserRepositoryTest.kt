package com.thongars.domain.repository

import com.thongars.data.database.dao.UserDao
import com.thongars.data.database.model.UserDetailEntity
import com.thongars.data.database.model.UserEntity
import com.thongars.domain.mapper.toDomain
import com.thongars.domain.mapper.toEntity
import com.thongars.domain.model.UserDetail
import com.thongars.utilities.test.BaseTestClassNoPowerMock
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocalUserRepositoryTest: BaseTestClassNoPowerMock() {

    private val dao: UserDao = mockk()

    private lateinit var repository: LocalUserRepository

    private val userEntities = listOf(
        UserEntity("user1", 1, "https://example.com/avatar1", "https://example.com/user1"),
        UserEntity("user2", 1, "https://example.com/avatar2", "https://example.com/user2")
    )

    private val mockedUserDetail = UserDetail(
        user = userEntities.first().toDomain(),
        blog = "blog.com",
        location = "HCM",
        followers = 10,
        following = 12
    )

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

    @Before
    override fun setUp() {
        super.setUp()
        repository = LocalUserRepository(dao)
    }

    @Test
    fun `insertAndUpdateUserListing should call upsertAll on dao`() = runTest {
        coEvery { dao.upsertAll(userEntities) } returns Unit

        repository.insertAndUpdateUserListing(userEntities)

        coVerify { dao.upsertAll(userEntities) }
    }

    @Test
    fun `insertUserDetail should call insertUserDetail on dao`() = runTest {

        coEvery { dao.insertUserDetail(mockedUserDetail.toEntity()) } returns Unit

        repository.insertUserDetail(mockedUserDetail)

        coVerify { dao.insertUserDetail(mockedUserDetail.toEntity()) }
    }

    @Test
    fun `getUserDetail should return UserDetailEntity from dao`() {
        val userDetailEntity = mockUserDetailEntities.first()
        val username = userDetailEntity.login
        coEvery { dao.getUserDetail(username) } returns userDetailEntity

        val result = repository.getUserDetail(username)

        assertEquals(userDetailEntity, result)
    }

    @Test
    fun `getUser should return UserEntity from dao`() {
        val userEntity = userEntities.last()
        val username = userEntity.login

        coEvery { dao.getUser(username) } returns userEntity

        val result = repository.getUser(username)

        assertEquals(userEntity, result)
    }

    @Test
    fun `getAllUserDetail should return Flow of UserDetailEntity list from dao`() = runTest {

        coEvery { dao.getAllUserDetail() } returns flowOf(mockUserDetailEntities)

        val result = repository.getAllUserDetail()

        result.collect { users ->
            assertEquals(mockUserDetailEntities, users)
        }
    }

    @Test
    fun `getLastInsertionTime should return insertion time from dao`() = runTest {
        val insertionTime = 123456789L

        coEvery { dao.getInsertionTime() } returns insertionTime

        val result = repository.getLastInsertionTime()

        assertEquals(insertionTime, result)
    }

    @Test
    fun `hasUserData should return hasData from dao`() = runTest {
        coEvery { dao.hasData() } returns true

        val result = repository.hasUserData()

        assertTrue(result)
    }

    @Test
    fun `clearAllUser should call clearAll on dao`() = runTest {
        coEvery { dao.clearAll() } returns Unit

        repository.clearAllUser()

        coVerify { dao.clearAll() }
    }
}
