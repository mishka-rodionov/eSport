package com.rodionov.center.presentation.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rodionov.center.data.CenterEffects
import org.koin.androidx.compose.koinViewModel

@Composable
fun CenterScreen(viewModel: CenterViewModel = koinViewModel()) {
    OutlinedButton(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        onClick = {
            viewModel.handleEffects(CenterEffects.OpenKindOfSports)
        },
        content = {
            Text("Создать новое событие")
        }
    )
}