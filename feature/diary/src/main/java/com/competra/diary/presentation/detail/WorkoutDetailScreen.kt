package com.competra.diary.presentation.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.competra.designsystem.theme.Dimens
import com.competra.diary.data.detail.WorkoutDetailAction
import com.competra.diary.presentation.common.DeleteConfirmationDialog
import com.competra.diary.presentation.common.formatWorkoutDuration
import com.competra.domain.models.diary.SportType
import com.competra.domain.models.diary.WorkoutStatus
import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.resources.R
import com.competra.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel

@Composable
fun WorkoutDetailScreen(
    workoutId: Long,
    viewModel: WorkoutDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(workoutId) {
        viewModel.load(workoutId)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(Dimens.SIZE_BASE.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.onAction(WorkoutDetailAction.BackClick) }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back_24px),
                        contentDescription = null
                    )
                }
                Text(
                    text = "Тренировка",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

            state.workout?.let { workout ->
                WorkoutDetailBody(workout)

                Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

                if (workout.workout.trackEncoded != null) {
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
                        onClick = { viewModel.onAction(WorkoutDetailAction.ViewTrackClick) }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_location_on_24px),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(Dimens.SIZE_HALF.dp))
                        Text("Посмотреть трек")
                    }
                    Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        onClick = { viewModel.onAction(WorkoutDetailAction.EditClick) }
                    ) {
                        Text("Редактировать")
                    }
                    Spacer(modifier = Modifier.width(Dimens.SIZE_HALF.dp))
                    OutlinedButton(
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        onClick = { viewModel.onAction(WorkoutDetailAction.DeleteClick) }
                    ) {
                        Text("Удалить")
                    }
                }
            }
        }
    }

    if (state.isDeleteDialogVisible) {
        DeleteConfirmationDialog(
            title = "Удалить тренировку?",
            onDismiss = { viewModel.onAction(WorkoutDetailAction.CancelDeleteClick) },
            onConfirm = { viewModel.onAction(WorkoutDetailAction.ConfirmDeleteClick) }
        )
    }
}

@Composable
private fun WorkoutDetailBody(workout: WorkoutWithDetails) {
    val w = workout.workout

    DetailRow(label = "Вид спорта", value = sportTypeLabel(w.sportType))
    DetailRow(
        label = if (w.status == WorkoutStatus.PLANNED) "Запланирована на" else "Дата",
        value = DateTimeFormat.transformLongToDisplayDate(w.startedAt ?: w.scheduledDate)
    )

    if (w.status == WorkoutStatus.COMPLETED) {
        w.durationSeconds?.let { DetailRow(label = "Длительность", value = formatWorkoutDuration(it)) }
        w.distanceMeters?.let { DetailRow(label = "Дистанция", value = "%.1f км".format(it / 1000.0)) }
        w.elevationGainMeters?.let { DetailRow(label = "Набор высоты", value = "$it м") }

        workout.runDetails?.cadenceSpm?.let { DetailRow(label = "Каденс", value = "$it шаг/мин") }
        workout.bikeDetails?.let { details ->
            details.cadenceRpm?.let { DetailRow(label = "Каденс", value = "$it об/мин") }
            details.powerWatts?.let { DetailRow(label = "Мощность", value = "$it Вт") }
        }
        workout.skiDetails?.let { details ->
            DetailRow(label = "Стиль", value = if (details.style.name == "CLASSIC") "Классика" else "Коньковый")
        }
    }

    w.notes?.takeIf { it.isNotBlank() }?.let { notes ->
        DetailRow(label = "Заметка", value = notes)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = Dimens.SIZE_HALF.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun sportTypeLabel(sportType: SportType): String = when (sportType) {
    SportType.RUNNING -> "Бег"
    SportType.CYCLING -> "Велоспорт"
    SportType.SKIING -> "Лыжи"
}
