package com.thongars.presentation.ui.route

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.thongars.domain.model.User
import com.thongars.presentation.R
import com.thongars.presentation.ui.serializableNavType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

object Screen {

    val screenTitleMap = mapOf(
        UserListing.route to R.string.user_list,
        UserDetail.route to R.string.user_details
    )

    @Serializable
    object UserListing {
        val route = this::class.qualifiedName
    }

    @Serializable
    data class UserDetail(
        val user: User
    ) {
        companion object {

            val route = UserDetail::class.qualifiedName + "/{user}"

            val typeMap = mapOf(
                typeOf<User>() to serializableNavType<User>(),
                typeOf<User?>() to serializableNavType<User?>()
            )

            fun from(savedStateHandle: SavedStateHandle) =
                savedStateHandle.toRoute<UserDetail>(typeMap)
        }
    }
}