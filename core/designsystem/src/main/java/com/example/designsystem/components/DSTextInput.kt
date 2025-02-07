package com.example.designsystem.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable

@Composable
fun DSTextInput(
    text: String = "",
    onValueChanged: (String) -> Unit = {}
) {
    OutlinedTextField(value = text, onValueChange = onValueChanged)
}
