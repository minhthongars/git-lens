package com.thongars.data.database

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.thongars.data.database.dao.UserDao
import com.thongars.data.database.model.UserEntity
import com.thongars.data.mapper.toDomain
import com.thongars.data.mapper.toEntity
import com.thongars.data.provider.UserContentProvider
import com.thongars.domain.DefaultDispatcher
import com.thongars.domain.IoDispatcher
import com.thongars.domain.model.User
import com.thongars.domain.model.UserDetail
import com.thongars.domain.repository.ContentResolveRepository
import com.thongars.domain.repository.LocalUserRepository
import com.thongars.utilities.ResourceState
import com.thongars.utilities.safeFetchDataCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalUserRepositoryImpl @Inject constructor(
    private val dao: UserDao,
    private val pager: Pager<Int, UserEntity>,
    private val contentResolver: ContentResolver?,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : LocalUserRepository, ContentResolveRepository {

    @SuppressLint("Range")
    private fun Cursor.createUser() = User(
        login = getString(getColumnIndex(DatabaseConstant.LOGIN_FOREIGN_KEY)),
        order = getInt(getColumnIndex(DatabaseConstant.COLUMN_ORDER)),
        avatarUrl = getString(getColumnIndex(DatabaseConstant.COLUMN_AVATAR)),
        htmlUrl = getString(getColumnIndex(DatabaseConstant.COLUMN_HTML)),
    )

    override fun getAllUsers(): Flow<ResourceState<List<User>>> {
        return safeFetchDataCall(
            dispatcher = ioDispatcher,
            fetchDataCall = {
                val cursor = contentResolver?.query(
                    UserContentProvider.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                )
                val userList = mutableListOf<User>()
                withContext(defaultDispatcher) {
                    cursor?.use { item ->
                        while (item.moveToNext()) {
                            userList.add(item.createUser())
                        }
                    }
                    userList
                }
            }
        )
    }

    override fun getSingleUser(username: String): Flow<ResourceState<User?>> {
        return safeFetchDataCall(
            dispatcher = ioDispatcher,
            fetchDataCall = {
                val uri = Uri.withAppendedPath(UserContentProvider.CONTENT_URI, username)
                val cursor = contentResolver?.query(uri, null, null, null, null)
                var user: User? = null
                cursor?.use { item ->
                    if (item.moveToFirst()) {
                        user = item.createUser()
                    }
                }
                user
            }
        )
    }

    override fun userPagingDataFlow(): Flow<PagingData<User>> {
        return pager.flow.map { pagingData ->
            pagingData.map { userEntity ->
                userEntity.toDomain()
            }
        }
            .flowOn(ioDispatcher)
    }

    override fun insertUserDetail(user: UserDetail): Flow<ResourceState<Unit>> {
        return safeFetchDataCall(
            dispatcher = ioDispatcher,
            fetchDataCall = {
                dao.insertUserDetail(user.toEntity())
            }
        )
    }

    override fun getUserDetail(username: String): Flow<ResourceState<UserDetail?>> {
        return safeFetchDataCall(
            dispatcher = ioDispatcher,
            fetchDataCall = {
                val user = dao.getUser(username)
                dao.getUserDetail(username)?.toDomain(user!!)
            }
        )

    }

    override fun getUser(username: String): Flow<ResourceState<User?>> {
        return safeFetchDataCall(
            dispatcher = ioDispatcher,
            fetchDataCall = {
                dao.getUser(username)?.toDomain()
            }
        )
    }

    override fun getAllUserDetail(): Flow<List<UserDetail>> {
        return dao.getAllUserDetail()
            .map { userDetailEntities ->
                userDetailEntities.map { userDetailEntity ->
                    val username = userDetailEntity.login
                    val user = dao.getUser(username)
                    userDetailEntity.toDomain(user!!)
                }
            }
            .flowOn(ioDispatcher)
    }

}