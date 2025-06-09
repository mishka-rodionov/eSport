package com.rodionov.center.presentation.orientiring_competition_create

import android.app.DatePickerDialog
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DSBottomDialog
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.components.TimePickerDialog
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.OrienteeringCreatorState
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun OrienteeringCompetitionCreator(viewModel: OrienteeringCreatorViewModel = koinViewModel()) {

    val state = viewModel.state.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        DSTextInput(modifier = Modifier.fillMaxWidth(),
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
        ParticipantGroup(state)
    }
}

@Composable
private fun ParticipantGroup(state: State<OrienteeringCreatorState>) {
    Text(text = "Группы")
    var showDialog by remember { mutableStateOf(false) }
    LazyColumn {
        items(state.value.participantGroups) {

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
        ParticipantGroupEditor {
            showDialog = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantGroupEditor(onExit: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var groupTitle by remember { mutableStateOf("") }
    var distance by remember { mutableDoubleStateOf(0.0) }
    var countOfControls by remember { mutableIntStateOf(0) }
    var sequenceOfControl by remember { mutableStateOf("") }
    var maxTime by remember { mutableIntStateOf(0) }
    DSBottomDialog(
        sheetState = sheetState,
        sheetContent = {
            Column(modifier = Modifier.padding(horizontal = Dimens.SIZE_HALF.dp)) {
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Название группы")
                    },
                    text = groupTitle,
                    onValueChanged = {
                        groupTitle = it
                    })
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Дистанция")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword ),
                    text = distance.takeIf { it != 0.0 }?.toString() ?: "",
                    onValueChanged = { dist ->
                        distance = dist.takeIf { it.isNotBlank() }?.trim()?.toDouble() ?: 0.0
                    })
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Кол-во КП")
                    },
                    text = countOfControls.takeIf { it != 0 }?.toString() ?: "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    onValueChanged = { coc ->
                        countOfControls = coc.takeIf { it.isNotBlank() }?.trim()?.toInt() ?: 0
                    })
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Порядок КП")
                    },
                    text = sequenceOfControl,
                    onValueChanged = { soc ->
                        sequenceOfControl = soc
                    })
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Контрольное время в мин.")
                    },
                    text = maxTime.takeIf { it != 0 }?.toString() ?: "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    onValueChanged = { time ->
                        maxTime = time.takeIf { it.isNotBlank() }?.trim()?.toInt() ?: 0
                    })
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    onExit.invoke()
                }) {
                    Text(text = "Сохранить группу")
                }
            }
        },
        onDismiss = {
            onExit.invoke()
        },
    )
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
            onDismissRequest = { showDialog = false
                focusManager.clearFocus()},
            onConfirm = { hour, minute ->
                selectedTime = "%02d:%02d".format(hour, minute)
                showDialog = false
                focusManager.clearFocus()
            }
        )
    }
}


