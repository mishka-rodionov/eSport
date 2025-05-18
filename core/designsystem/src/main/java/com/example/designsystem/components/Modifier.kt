package com.example.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.ripple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.example.designsystem.theme.DSRippleTheme.rippleConfiguration

/**
 * Modifier для добавления нажатия с рипплом
 */
@OptIn(ExperimentalMaterial3Api::class)
fun Modifier.clickRipple(
    enabled: Boolean = true,
    bounded: Boolean = true,
    onClick: () -> Unit
) = composed {
    val rippleColor = LocalRippleConfiguration.current ?: rippleConfiguration
    this.clickable(
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = bounded, color = rippleColor.color),
        onClick = onClick
    )
}