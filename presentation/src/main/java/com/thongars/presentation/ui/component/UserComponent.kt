package com.thongars.presentation.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thongars.presentation.model.User

@Composable
fun UserItem(
    user: User,
    onUserClick: (User) -> Unit = { _ -> },
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(8.dp),
                clip = false,
                spotColor = MaterialTheme.colorScheme.onBackground
            )
            .clickable { onUserClick(user) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppImage(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape),
                url = user.avatarUrl,
                backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.height(128.dp)
            ) {
                Text(
                    text = user.userName,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                content()
            }
        }
    }
}

@Composable
fun FooterMessage(message: String) {
    Text(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        text = message,
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewUserItem() {
    val sampleUser = User(
        userName = "Minh Thông",
        avatarUrl = "",
        landingPageUrl = "tymex.com"
    )
    Box(modifier = Modifier.padding(20.dp)) {
        UserItem(user = sampleUser) {}
    }
}
