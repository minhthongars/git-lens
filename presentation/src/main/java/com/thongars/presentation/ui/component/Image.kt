package com.thongars.presentation.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.thongars.presentation.R

@Composable
fun AppImage(
    modifier: Modifier = Modifier,
    url: String?,
    @DrawableRes placeholder: Int = R.drawable.placeholder,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    backgroundColor: Color? = null
) {
    val painter = if (LocalInspectionMode.current) {
        painterResource(placeholder)
    } else {
        rememberAsyncImagePainter(
            model = url ?: placeholder,
            placeholder = painterResource(placeholder),
            error = painterResource(placeholder)
        )
    }

    Image(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
            .background(
                color = backgroundColor ?: Color.Transparent
            ),
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAppImageWithUrl() {
    AppImage(
        url = "https://placeholder.com/128",
        placeholder = R.drawable.placeholder,
        contentScale = ContentScale.Crop,
        modifier = Modifier.height(40.dp).width(30.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAppImageWithoutUrl() {
    AppImage(
        url = null,
        placeholder = R.drawable.placeholder,
        backgroundColor = Color.Red,
        modifier = Modifier.height(100.dp).width(100.dp)
    )
}