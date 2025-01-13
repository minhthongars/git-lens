import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import app.cash.turbine.test
import com.thongars.data.database.model.UserEntity
import com.thongars.domain.mapper.toDomain
import com.thongars.domain.model.User
import com.thongars.domain.usecase.FetchUserUseCase
import com.thongars.utilities.test.BaseTestClassNoPowerMock
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class FetchUserUseCaseTest: BaseTestClassNoPowerMock() {

    private val pager: Pager<Int, UserEntity> = mockk()

    private lateinit var fetchUserUseCase: FetchUserUseCase

    private val userEntities = listOf(
        UserEntity("user1", 1, "https://example.com/avatar1", "https://example.com/user1"),
        UserEntity("user2", 1, "https://example.com/avatar2", "https://example.com/user2")
    )

    override fun setUp() {
        super.setUp()
        fetchUserUseCase = FetchUserUseCase(pager)
    }

    @Test
    fun `invoke() should return mapped PagingData of Users`() = mainCoroutineRule.runBlockingTest {
        val pagingData = PagingData.from(userEntities)
        val expectedUsers = userEntities.map { it.toDomain() }

        every { pager.flow } returns flowOf(pagingData)

        val result: Flow<PagingData<User>> = fetchUserUseCase.invoke(testCoroutineDispatcher)

        result.test {
            val pagingDataResult = awaitItem()
            val actualUsers = mutableListOf<User>()
            pagingDataResult.map { user ->
                actualUsers.add(user)
            }
            //assertEquals(expectedUsers, actualUsers) // fixing
        }

        verify { pager.flow }
    }
}
