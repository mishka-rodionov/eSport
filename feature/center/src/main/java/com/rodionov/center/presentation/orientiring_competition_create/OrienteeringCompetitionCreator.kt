package com.rodionov.center.presentation.orientiring_competition_create

import android.app.DatePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.components.ExposedDropdownMenuOutlined
import com.example.designsystem.components.TimePickerDialog
import com.rodionov.center.data.OrienteeringCreatorAction
import com.rodionov.center.data.OrienteeringCreatorState
import com.rodionov.domain.models.OrienteeringDirection
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.resources.R
import com.rodionov.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.util.Calendar

@Composable
fun OrienteeringCompetitionCreator(viewModel: OrienteeringCreatorViewModel = koinViewModel()) {

    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = state.title,
            label = {
                Text(text = stringResource(R.string.label_competition_name))
            },
            supportingText = {
                Text(
                    text = stringResource(
                        R.string.label_competition_name_description,
                        DateTimeFormat.formatDate(state.date)
                    )
                )
            },
            onValueChanged = {
                viewModel.updateState { copy(title = it) }
            })
        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = stringResource(R.string.label_competition_place))
            },
            isError = state.errors.isEmptyAddress,
            supportingText = {
                if (state.errors.isEmptyAddress) {
                    Text(text = stringResource(R.string.error_competition_place))
                }
            },
            text = state.address,
            onValueChanged = {
                viewModel.updateState { copy(address = it) }
            })
        DatePicker(state = state, userAction = viewModel::onUserAction)
        TimePicker(state = state, userAction = viewModel::onUserAction)
        OrienteeringCompetitionDirection(state = state, userAction = viewModel::onUserAction)
        DSTextInput(
            modifier = Modifier
                .fillMaxWidth(),
            onValueChanged = {
                viewModel.updateState { copy(description = it) }
            },
            text = state.description,
            label = {
                Text(text = stringResource(R.string.label_competition_description))
            },
            placeholder = {
                Text(text = stringResource(R.string.hint_competition_description))
            }
        )
        ParticipantGroupContent(state = state, userAction = viewModel::onUserAction)
        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
            viewModel.onUserAction(OrienteeringCreatorAction.Apply)
        }) {
            Text(text = stringResource(R.string.label_apply))
        }
    }
}

@Composable
private fun ParticipantGroupContent(
    state: OrienteeringCreatorState,
    userAction: (OrienteeringCreatorAction) -> Unit
) {
    Text(
        text = stringResource(R.string.label_groups),
        color = if (state.errors.isEmptyGroup) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    )
    if (state.errors.isEmptyGroup) {
        Text(
            text = stringResource(R.string.label_add_at_least_group),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.error
        )
    }
    LazyRow {
        itemsIndexed(state.participantGroups) { index, item ->
            GroupContent(item, userAction, index)
            if (index != state.participantGroups.size - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
    OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
        userAction.invoke(OrienteeringCreatorAction.ShowGroupCreateDialog)
    }) {
        Text(text = stringResource(R.string.label_add_group))
    }
    if (state.isShowGroupCreateDialog) {
        ParticipantGroupEditor(
            userAction = userAction,
            state = state
        )
    }
}

@Composable
fun GroupContent(
    participantGroup: ParticipantGroup,
    userAction: (OrienteeringCreatorAction) -> Unit,
    groupIndex: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        Column(
//            modifier = Modifier.weight(1F)
        ) {
            Text(text = stringResource(R.string.label_group_name, participantGroup.title))
            Text(text = stringResource(R.string.label_distance, participantGroup.distance))
            Text(
                text = stringResource(
                    R.string.label_count_of_controls,
                    participantGroup.countOfControls
                )
            )
            Text(text = stringResource(R.string.label_max_time, participantGroup.maxTimeInMinute))
        }
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = {
                userAction.invoke(OrienteeringCreatorAction.EditGroupDialog(groupIndex))
            }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "group edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = {
                userAction.invoke(OrienteeringCreatorAction.DeleteGroup(index = groupIndex))
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "group delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun DatePicker(state: OrienteeringCreatorState, userAction: (OrienteeringCreatorAction) -> Unit) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val date = LocalDate.of(year, month + 1, dayOfMonth)
                userAction.invoke(OrienteeringCreatorAction.UpdateCompetitionDate(date))
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
            Text(text = stringResource(R.string.label_date))
        },
        text = DateTimeFormat.formatDate(state.date),
        interactionSource = interactionSource,
        enabled = true,
        readOnly = true
    )

}

@Composable
fun TimePicker(state: OrienteeringCreatorState, userAction: (OrienteeringCreatorAction) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    DSTextInput(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                showDialog = focusState.isFocused
            },
        label = {
            Text(text = stringResource(R.string.label_time))
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
                userAction.invoke(
                    OrienteeringCreatorAction.UpdateCompetitionTime(
                        "%02d:%02d".format(
                            hour,
                            minute
                        )
                    )
                )
                showDialog = false
                focusManager.clearFocus()
            }
        )
    }
}

@Composable
private fun OrienteeringCompetitionDirection(
    state: OrienteeringCreatorState,
    userAction: (OrienteeringCreatorAction) -> Unit
) {
    val context = LocalContext.current
    ExposedDropdownMenuOutlined(
        label = stringResource(R.string.label_direction),
        items = OrienteeringDirection.entries,
        selectedItem = state.competitionDirection,
        onItemSelected = {
            userAction.invoke(OrienteeringCreatorAction.UpdateCompetitionDirection(it))
        },
        itemToString = {
            when(it) {
                OrienteeringDirection.FORWARD -> context.getString(R.string.label_direction_forward)
                OrienteeringDirection.BY_CHOICE -> context.getString(R.string.label_direction_by_choice)
                OrienteeringDirection.MARKING -> context.getString(R.string.label_direction_marking)
            }
        }
    )
}


