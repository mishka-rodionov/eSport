package com.rodionov.center.presentation.orientiring_competition_create

import android.R.attr.text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.designsystem.components.DSTextInput
import org.koin.androidx.compose.koinViewModel

@Composable
fun OrienteeringCompetitionCreator(viewModel: OrienteeringCompetitionCreatorViewModel = koinViewModel()) {

    val state = viewModel.state.collectAsState()

    Column {
        Text(text = "Название соревнования")
        DSTextInput(modifier = Modifier.fillMaxWidth(), text = state.value.title, onValueChanged = {
            viewModel.updateState { copy(title = it) }
        })
        Text(text = "Место проведения")
        DSTextInput(modifier = Modifier.fillMaxWidth(), text = state.value.address, onValueChanged = {
            viewModel.updateState { copy(address = it) }
        })
        Text(text = "Дата")
        DSTextInput(modifier = Modifier.fillMaxWidth(), text = state.value.date.toString(), onValueChanged = {
//            viewModel.updateState { copy(date = it.to) }
        })
    }
}