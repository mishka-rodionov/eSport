package com.competra.diary.presentation.editor

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.competra.designsystem.components.DSTextInput
import com.competra.designsystem.components.ExposedDropdownMenuOutlined
import com.competra.designsystem.theme.Dimens
import com.competra.diary.data.editor.WorkoutEditorAction
import com.competra.diary.data.editor.WorkoutEditorState
import com.competra.domain.models.diary.SkiStyle
import com.competra.domain.models.diary.SportType
import com.competra.domain.models.diary.WorkoutStatus
import com.competra.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@Composable
fun WorkoutEditorScreen(
    workoutId: Long?,
    viewModel: WorkoutEditorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(workoutId) {
        viewModel.load(workoutId)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        WorkoutEditorContent(state, viewModel::onAction)
    }
}

@Composable
private fun WorkoutEditorContent(state: WorkoutEditorState, onAction: (WorkoutEditorAction) -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.SIZE_BASE.dp)
    ) {
        Text(
            text = if (state.workoutId == null) "Новая тренировка" else "Редактирование тренировки",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

        ExposedDropdownMenuOutlined(
            modifier = Modifier.fillMaxWidth(),
            label = "Вид спорта",
            items = listOf(SportType.RUNNING, SportType.CYCLING, SportType.SKIING),
            selectedItem = state.sportType,
            onItemSelected = { onAction(WorkoutEditorAction.SelectSportType(it)) },
            itemToString = ::sportTypeLabel
        )

        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            FilterChip(
                selected = state.status == WorkoutStatus.COMPLETED,
                onClick = { onAction(WorkoutEditorAction.SelectStatus(WorkoutStatus.COMPLETED)) },
                label = { Text("Выполнена") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(Dimens.SIZE_HALF.dp))
            FilterChip(
                selected = state.status == WorkoutStatus.PLANNED,
                onClick = { onAction(WorkoutEditorAction.SelectStatus(WorkoutStatus.PLANNED)) },
                label = { Text("Запланирована") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = DateTimeFormat.transformLongToDisplayDate(state.dateMillis),
            onValueChanged = {},
            readOnly = true,
            label = { Text(if (state.status == WorkoutStatus.PLANNED) "Дата тренировки" else "Дата") },
            trailingIcon = null
        )

        Row {
            Button(onClick = {
                val calendar = Calendar.getInstance().apply { timeInMillis = state.dateMillis }
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val picked = Calendar.getInstance().apply { set(year, month, day) }
                        onAction(WorkoutEditorAction.ChangeDate(picked.timeInMillis))
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text("Изменить дату")
            }
        }

        if (state.status == WorkoutStatus.COMPLETED) {
            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.durationMinutesInput,
                onValueChanged = { onAction(WorkoutEditorAction.ChangeDuration(it)) },
                label = { Text("Длительность, мин") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.distanceKmInput,
                onValueChanged = { onAction(WorkoutEditorAction.ChangeDistance(it)) },
                label = { Text("Дистанция, км") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.elevationGainInput,
                onValueChanged = { onAction(WorkoutEditorAction.ChangeElevationGain(it)) },
                label = { Text("Набор высоты, м") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            SportSpecificFields(state, onAction)
        }

        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = state.notes,
            onValueChanged = { onAction(WorkoutEditorAction.ChangeNotes(it)) },
            label = { Text("Заметка") }
        )

        Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !state.isSaving,
            shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            onClick = { onAction(WorkoutEditorAction.Save) }
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(text = "Сохранить", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
    }
}

@Composable
private fun SportSpecificFields(state: WorkoutEditorState, onAction: (WorkoutEditorAction) -> Unit) {
    when (state.sportType) {
        SportType.RUNNING -> DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = state.cadenceSpmInput,
            onValueChanged = { onAction(WorkoutEditorAction.ChangeCadenceSpm(it)) },
            label = { Text("Каденс, шаг/мин") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        SportType.CYCLING -> Column {
            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.cadenceRpmInput,
                onValueChanged = { onAction(WorkoutEditorAction.ChangeCadenceRpm(it)) },
                label = { Text("Каденс, об/мин") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.powerWattsInput,
                onValueChanged = { onAction(WorkoutEditorAction.ChangePowerWatts(it)) },
                label = { Text("Мощность, Вт") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
        SportType.SKIING -> Row(modifier = Modifier.fillMaxWidth()) {
            FilterChip(
                selected = state.skiStyle == SkiStyle.CLASSIC,
                onClick = { onAction(WorkoutEditorAction.SelectSkiStyle(SkiStyle.CLASSIC)) },
                label = { Text("Классика") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(Dimens.SIZE_HALF.dp))
            FilterChip(
                selected = state.skiStyle == SkiStyle.SKATE,
                onClick = { onAction(WorkoutEditorAction.SelectSkiStyle(SkiStyle.SKATE)) },
                label = { Text("Коньковый") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun sportTypeLabel(sportType: SportType): String = when (sportType) {
    SportType.RUNNING -> "Бег"
    SportType.CYCLING -> "Велоспорт"
    SportType.SKIING -> "Лыжи"
}
