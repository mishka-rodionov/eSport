package com.rodionov.center.presentation.orientiring_competition_create

import android.app.DatePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.components.TimePickerDialog
import com.rodionov.center.data.OrienteeringCreatorEffects
import com.rodionov.center.data.OrienteeringCreatorState
import com.rodionov.domain.models.ParticipantGroup
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun OrienteeringCompetitionCreator(viewModel: OrienteeringCreatorViewModel = koinViewModel()) {

    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(scrollState)) {
        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = state.title,
            label = {
                Text(text = "Название соревнования")
            },
            onValueChanged = {
                viewModel.updateState { copy(title = it) }
            })
        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Место проведения")
            },
            text = state.address,
            onValueChanged = {
                viewModel.updateState { copy(address = it) }
            })
        DatePicker(state = state, userAction = viewModel::onUserAction)
        TimePicker(state = state, userAction = viewModel::onUserAction)
        DSTextInput(
            modifier = Modifier
                .fillMaxWidth(),
            onValueChanged = {
                viewModel.updateState { copy(description = it) }
            },
            text = state.description,
            label = {
                Text(text = "Описание")
            },
            placeholder = {
                Text(text = "Описание соревнования.")
            }
        )
        ParticipantGroupContent(state = state, userAction = viewModel::onUserAction)
        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
            viewModel.onUserAction(OrienteeringCreatorEffects.Apply)
        }) {
            Text(text = "Готово")
        }
    }
}

@Composable
private fun ParticipantGroupContent(
    state: OrienteeringCreatorState,
    userAction: (OrienteeringCreatorEffects) -> Unit
) {
    Text(text = "Группы")
    var showDialog by remember { mutableStateOf(false) }
    LazyRow {
        itemsIndexed(state.participantGroups) { index, item ->
            GroupContent(item)
            if (index != state.participantGroups.size - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
    OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
        showDialog = true
    }) {
        Text(text = "Добавить группу ")
    }
    if (showDialog) {
        ParticipantGroupEditor(onExit = {
            showDialog = false
        }, userAction = userAction)
    }
}

@Composable
fun GroupContent(participantGroup: ParticipantGroup) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        Text(text = "Название группы: ${participantGroup.title}")
        Text(text = "Дистанция: ${participantGroup.distance} км.")
        Text(text = "Кол-во КП: ${participantGroup.countOfControls}")
        Text(text = "Порядок КП: \n ${participantGroup.sequenceOfControl.joinToString(",")}")
        Text(text = "Контрольное время: ${participantGroup.maxTimeInMinute} мин.")
    }
}

@Composable
fun DatePicker(state: OrienteeringCreatorState, userAction: (OrienteeringCreatorEffects) -> Unit) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val formatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val date = LocalDate.of(year, month + 1, dayOfMonth)
                userAction.invoke(OrienteeringCreatorEffects.UpdateCompetitionDate(date))
                focusManager.clearFocus()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    datePickerDialog.setOnDismissListener { focusManager.clearFocus() }

    DSTextInput(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    datePickerDialog.show()
                }
            },
        label = {
            Text(text = "Дата")
        },
        text = state.date.format(formatter),
        interactionSource = interactionSource,
        enabled = true,
        readOnly = true
    )

}

@Composable
fun TimePicker(state: OrienteeringCreatorState, userAction: (OrienteeringCreatorEffects) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    DSTextInput(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                showDialog = focusState.isFocused
            },
        label = {
            Text(text = "Время")
        },
        text = state.time,
        enabled = true,
        readOnly = true
    )

    if (showDialog) {
        TimePickerDialog(
            onDismissRequest = {
                showDialog = false
                focusManager.clearFocus()
            },
            onConfirm = { hour, minute ->
                userAction.invoke(OrienteeringCreatorEffects.UpdateCompetitionTime("%02d:%02d".format(hour, minute)))
                showDialog = false
                focusManager.clearFocus()
            }
        )
    }
}


