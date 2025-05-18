package com.example.designsystem.theme

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RippleConfiguration
import com.example.designsystem.colors.LightColors

/**
 * Кастомная обертка для ripple'а
 */
object DSRippleTheme {

    private val lightThemeRipple = RippleAlpha(
        pressedAlpha = 0.2f,
        focusedAlpha = 0.2f,
        draggedAlpha = 0.08f,
        hoveredAlpha = 0.1f,
    )

    @OptIn(ExperimentalMaterial3Api::class)
    val rippleConfiguration = RippleConfiguration(
        color = LightColors.greyB8,
        rippleAlpha = lightThemeRipple
    )
}