package com.rodionov.center.presentation.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.rodionov.center.data.CenterEffects
import com.rodionov.center.navigation.CenterNavigationGraph
import org.koin.androidx.compose.koinViewModel

@Composable
fun CenterScreen(viewModel: CenterViewModel = koinViewModel()) {
    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            viewModel.handleEffects(CenterEffects.OpenKindOfSports)
        },
        content = {
            Text("Go to kind of sport")
        }
    )
}