package com.rodionov.center.presentation.read_card

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrientReadCardScreen(viewModel: OrientReadCardViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Text(text = state.text)
}