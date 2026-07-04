package com.competra.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import kotlin.math.max

private const val MIN_SCALE = 1f
private const val MAX_SCALE = 5f
private const val DOUBLE_TAP_SCALE = 3f

/**
 * Полноэкранный просмотр изображения с pinch-to-zoom и панорамированием.
 * Одиночный тап закрывает просмотрщик, но только пока изображение не увеличено —
 * иначе он мешал бы панорамированию. Двойной тап переключает между обычным
 * масштабом и приближением. Также закрывается системной кнопкой "назад".
 *
 * @param url URL изображения.
 * @param contentDescription Описание для accessibility.
 * @param onDismiss Колбек при закрытии просмотрщика.
 */
@Composable
fun FullscreenImageViewer(
    url: String,
    contentDescription: String?,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        var scale by remember { mutableFloatStateOf(MIN_SCALE) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        var containerSize by remember { mutableStateOf(IntSize.Zero) }

        fun clampOffset(candidate: Offset, currentScale: Float): Offset {
            val maxX = max(0f, containerSize.width * (currentScale - 1f) / 2f)
            val maxY = max(0f, containerSize.height * (currentScale - 1f) / 2f)
            return Offset(candidate.x.coerceIn(-maxX, maxX), candidate.y.coerceIn(-maxY, maxY))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .onSizeChanged { containerSize = it }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (scale <= MIN_SCALE) onDismiss()
                        },
                        onDoubleTap = {
                            if (scale > MIN_SCALE) {
                                scale = MIN_SCALE
                                offset = Offset.Zero
                            } else {
                                scale = DOUBLE_TAP_SCALE
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(MIN_SCALE, MAX_SCALE)
                        scale = newScale
                        offset = if (newScale <= MIN_SCALE) {
                            Offset.Zero
                        } else {
                            clampOffset(offset + pan, newScale)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = url,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    ),
                contentScale = ContentScale.Fit
            )
        }
    }
}
