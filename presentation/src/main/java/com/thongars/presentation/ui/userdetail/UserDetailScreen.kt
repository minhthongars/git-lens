package com.thongars.presentation.ui.userdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thongars.presentation.R
import com.thongars.presentation.model.User
import com.thongars.presentation.model.UserDetail
import com.thongars.presentation.ui.component.ErrorScreenComponent
import com.thongars.presentation.ui.component.UserItem

@Composable
fun UserDetailScreen(viewModel: UserDetailViewModel = hiltViewModel()) {

    val uiState: UserDetailViewModel.UiState? = viewModel
        .uiState
        .collectAsStateWithLifecycle()
        .value

    when(uiState) {
        is UserDetailViewModel.UiState.Success -> {
            UserDetailContent(userDetail = uiState.data)
        }
        is UserDetailViewModel.UiState.Error -> {
            ErrorScreenComponent(message = uiState.message) {
                viewModel.loadData()
            }
        }
        else -> Unit
    }

}

@Composable
fun UserDetailContent(userDetail: UserDetail) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserItem(user = userDetail.user) {
            LocationItem(userDetail.location)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {


            FollowInfoItem(
                content = stringResource(id = R.string.followers, userDetail.followers),
                icon = Icons.Default.AccountCircle
            )

            FollowInfoItem(
                content =  stringResource(id = R.string.following, userDetail.following),
                icon = Icons.Default.Person
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(id = R.string.blog),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = userDetail.blog,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun FollowInfoItem(
    content: String,
    icon: ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun LocationItem(location: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
        Text(text = location)
    }
}

@Preview(showBackground = true)
@Composable
fun UserDetailPreView() {

    val sampleUserDetail = UserDetail(
        user = User(
            userName = "Thông",
            avatarUrl = "https://placeholder.com",
            landingPageUrl = "https://linkedin.com/"
        ),
        location = "Vietnam",
        followers = "100",
        following = "10",
        blog = "https://linkedin.com/"
    )

    UserDetailContent(userDetail = sampleUserDetail)
}