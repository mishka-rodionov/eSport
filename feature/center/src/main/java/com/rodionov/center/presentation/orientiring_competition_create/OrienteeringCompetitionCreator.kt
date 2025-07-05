package com.rodionov.center.presentation.orientiring_competition_create

import android.app.DatePickerDialog
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

    val state = viewModel.state.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = state.value.title,
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
            text = state.value.address,
            onValueChanged = {
                viewModel.updateState { copy(address = it) }
            })
        DatePicker()
        TimePicker()
        DSTextInput(
            modifier = Modifier
                .fillMaxWidth(),
            onValueChanged = {
                viewModel.updateState { copy(description = it) }
            },
            text = state.value.description,
            label = {
                Text(text = "Описание")
            },
            placeholder = {
                Text(text = "Описание соревнования.")
            }
        )
        ParticipantGroupContent(state = state, userAction = viewModel::onUserAction)
    }
}

@Composable
private fun ParticipantGroupContent(
    state: State<OrienteeringCreatorState>,
    userAction: (OrienteeringCreatorEffects) -> Unit
) {
    Text(text = "Группы")
    var showDialog by remember { mutableStateOf(false) }
    LazyColumn {
        items(state.value.participantGroups) {
            GroupContent(it)
        }
        item {
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                showDialog = true
            }) {
                Text(text = "Добавить группу ")
            }
        }
    }
    if (showDialog) {
        ParticipantGroupEditor(onExit = {
            showDialog = false
        }, userAction = userAction)
    }
}

@Composable
fun GroupContent(participantGroup: ParticipantGroup) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Название группы: ${participantGroup.title}")
        Text(text = "Дистанция: ${participantGroup.distance} км.")
        Text(text = "Кол-во КП: ${participantGroup.countOfControls}")
        Text(text = "Порядок КП: \n ${participantGroup.sequenceOfControl.joinToString(",")}")
        Text(text = "Контрольное время: ${participantGroup.maxTimeInMinute} мин.")
    }
}

@Composable
fun DatePicker() {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val formatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    var selectedDate by remember { mutableStateOf(LocalDate.now().format(formatter)) }
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val date = LocalDate.of(year, month + 1, dayOfMonth)
                selectedDate = date.format(formatter)
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
        text = selectedDate,
        interactionSource = interactionSource,
        enabled = true,
        readOnly = true
    )

}

@Composable
fun TimePicker() {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("12:00") }

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
        text = selectedTime,
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
                selectedTime = "%02d:%02d".format(hour, minute)
                showDialog = false
                focusManager.clearFocus()
            }
        )
    }
}


