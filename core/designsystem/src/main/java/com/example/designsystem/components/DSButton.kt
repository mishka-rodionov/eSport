package com.example.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DSButton(
    text: CharSequence,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    @DrawableRes iconLeft: Int = 0,
    @DrawableRes iconRight: Int = 0,
    onClick: () -> Unit = {},
) {
    Button(
        modifier = modifier,
        enabled = isEnabled,
        onClick = onClick.takeIf { !isLoading } ?: {},
        shape = RoundedCornerShape(12.dp),
//        contentPadding = contentPadding,
//        colors = style.buttonColors(),
//        border = style.border.takeIf { isEnabled },
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        } else {
            Text(text = text.toString())
        }
    }
}