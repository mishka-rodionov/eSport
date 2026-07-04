package com.competra.app.presentation.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.competra.app.R
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 1500L
private const val SHIMMER_DURATION_MS = 1800
private val LOGO_SIZE = 320.dp

// Фирменный градиент лого (см. ic_launcher_foreground.xml) + бегущий по нему блик.
private val LogoGradientStart = Color(0xFFFF9A3D)
private val LogoGradientHighlight = Color(0xFFFFD37A)
private val LogoGradientEnd = Color(0xFFE54B0A)

/**
 * Сплэш-экран при холодном старте: лого приложения с переливающимся бликом
 * на фиксированное время, без реальной инициализации внутри.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        onFinished()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            ShimmeringLogo(modifier = Modifier.size(LOGO_SIZE))
        }
    }
}

/**
 * Лого с переливающимся оранжевым градиентом: поверх контента рисуется свой
 * анимированный градиент в фирменных тонах с [BlendMode.SrcIn] — он подменяет
 * заливку иконки, но сохраняет её силуэт (альфа-канал вектора).
 */
@Composable
private fun ShimmeringLogo(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_logo_shimmer")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SHIMMER_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_progress"
    )

    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_launcher_foreground),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = modifier
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
            drawContent()
            val bandWidth = size.width * 0.9f
            val start = size.width * shimmerProgress - bandWidth / 2f
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(LogoGradientStart, LogoGradientHighlight, LogoGradientEnd),
                    start = Offset(start, 0f),
                    end = Offset(start + bandWidth, size.height)
                ),
                blendMode = BlendMode.SrcIn
            )
        }
    )
}
