package com.rodionov.center.presentation.orientiring_competition_create

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.components.TimePickerDialog
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.center.data.creator.OrienteeringCreatorState
import com.rodionov.resources.R
import com.rodionov.utils.DateTimeFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

/**
 * Компонент выбора даты.
 */
@Composable
fun DatePicker(state: OrienteeringCreatorState, userAction: (OrienteeringCreatorAction) -> Unit) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val focusManager = LocalFocusManager.current

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val dateMillis = LocalDate.of(year, month + 1, dayOfMonth)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
                userAction.invoke(OrienteeringCreatorAction.UpdateCompetitionDate(dateMillis))
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
            .onFocusChanged { if (it.isFocused) datePickerDialog.show() },
        label = { Text(text = stringResource(R.string.label_date)) },
        text = DateTimeFormat.transformLongToDisplayDate(state.startDate),
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_date_range_24px),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}

/**
 * Компонент выбора времени.
 */
@Composable
fun TimePicker(state: OrienteeringCreatorState, userAction: (OrienteeringCreatorAction) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    DSTextInput(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { showDialog = it.isFocused },
        label = { Text(text = stringResource(R.string.label_time)) },
        text = state.startTimeStr,
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_build_24px), // Используем как заглушку для времени
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )

    if (showDialog) {
        TimePickerDialog(
            onDismissRequest = {
                showDialog = false
                focusManager.clearFocus()
            },
            onConfirm = { hour, minute ->
                userAction.invoke(
                    OrienteeringCreatorAction.UpdateCompetitionTime("%02d:%02d".format(hour, minute))
                )
                showDialog = false
                focusManager.clearFocus()
            }
        )
    }
}
