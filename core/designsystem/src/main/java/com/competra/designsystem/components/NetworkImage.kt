package com.competra.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

/**
 * Компонент для отображения изображений из сети с fallback-заглушкой.
 *
 * @param url URL изображения (null — показывает цветную заглушку из темы).
 * @param modifier Модификатор.
 * @param contentScale Способ масштабирования.
 * @param contentDescription Описание для accessibility.
 * @param fullscreenOnClick Открывать ли изображение на весь экран по тапу.
 * Выключай там, где по картинке уже назначен другой клик (например, весь ряд списка).
 */
@Composable
fun NetworkImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    fullscreenOnClick: Boolean = true
) {
    var isFullscreenVisible by remember { mutableStateOf(false) }

    if (url.isNullOrBlank()) {
        Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant))
    } else {
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            modifier = if (fullscreenOnClick) {
                modifier.clickable { isFullscreenVisible = true }
            } else {
                modifier
            },
            contentScale = contentScale,
            placeholder = null,
            error = null
        )

        if (isFullscreenVisible) {
            FullscreenImageViewer(
                url = url,
                contentDescription = contentDescription,
                onDismiss = { isFullscreenVisible = false }
            )
        }
    }
}
