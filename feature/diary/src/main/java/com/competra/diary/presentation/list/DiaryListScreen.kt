package com.competra.diary.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.competra.designsystem.theme.Dimens
import com.competra.diary.data.list.DiaryListAction
import com.competra.diary.data.list.DiaryListState
import com.competra.diary.presentation.common.DeleteConfirmationDialog
import com.competra.domain.models.diary.SportType
import com.competra.domain.models.diary.WorkoutStatus
import com.competra.domain.models.diary.WorkoutWithDetails
import com.competra.resources.R
import com.competra.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel

@Composable
fun DiaryListScreen(viewModel: DiaryListViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    // LocalLifecycleOwner здесь — lifecycle конкретного NavBackStackEntry этого роута,
    // а не Activity, поэтому ON_RESUME срабатывает и при возврате из редактора/карточки
    // тренировки (Compose Navigation не перевызывает composable-лямбду при pop back stack).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadWorkouts()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        DiaryListContent(state, viewModel::onAction)
    }

    state.deletingWorkout?.let { workout ->
        DeleteConfirmationDialog(
            title = "Удалить тренировку?",
            onDismiss = { viewModel.onAction(DiaryListAction.HideDeleteDialog) },
            onConfirm = { viewModel.onAction(DiaryListAction.DeleteWorkout(workout)) }
        )
    }
}

@Composable
private fun DiaryListContent(state: DiaryListState, onAction: (DiaryListAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.SIZE_BASE.dp)
    ) {
        Text(
            text = "Тренировочный дневник",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = Dimens.SIZE_BASE.dp)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = Dimens.SIZE_BASE.dp),
            onClick = { onAction(DiaryListAction.OpenCreateWorkout) },
            shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_add_24px),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Добавить тренировку", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

        val contentMode = when {
            state.isLoading -> DiaryContentMode.LOADING
            state.workouts.isEmpty() -> DiaryContentMode.EMPTY
            else -> DiaryContentMode.LIST
        }

        Crossfade(targetState = contentMode, label = "diary_list_content") { mode ->
            when (mode) {
                DiaryContentMode.LOADING -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                DiaryContentMode.EMPTY -> EmptyDiaryView()
                DiaryContentMode.LIST -> WorkoutList(state.workouts, onAction)
            }
        }
    }
}

private enum class DiaryContentMode { LOADING, EMPTY, LIST }

@Composable
private fun WorkoutList(workouts: List<WorkoutWithDetails>, onAction: (DiaryListAction) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp),
        contentPadding = PaddingValues(bottom = Dimens.SIZE_BASE.dp)
    ) {
        items(workouts, key = { it.workout.id }) { workout ->
            WorkoutCard(
                workout = workout,
                onAction = onAction,
                modifier = Modifier.animateItem(placementSpec = tween(durationMillis = 300))
            )
        }
    }
}

@Composable
private fun WorkoutCard(
    workout: WorkoutWithDetails,
    onAction: (DiaryListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val w = workout.workout
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(Dimens.SIZE_BASE.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sportTypeLabel(w.sportType),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (w.status == WorkoutStatus.PLANNED) {
                        Text(
                            text = "  •  запланировано",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = DateTimeFormat.transformLongToDisplayDate(w.startedAt ?: w.scheduledDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (w.status == WorkoutStatus.COMPLETED) {
                    Text(
                        text = workoutSummary(workout),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = { onAction(DiaryListAction.OpenWorkoutDetails(workout)) }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_chevron_forward_24px),
                    contentDescription = null
                )
            }
        }
    }
}

private fun sportTypeLabel(sportType: SportType): String = when (sportType) {
    SportType.RUNNING -> "Бег"
    SportType.CYCLING -> "Велоспорт"
    SportType.SKIING -> "Лыжи"
}

private fun workoutSummary(workout: WorkoutWithDetails): String {
    val distanceKm = workout.workout.distanceMeters?.let { it / 1000.0 }
    val minutes = workout.workout.durationSeconds?.let { it / 60 }
    return buildString {
        if (distanceKm != null) append("%.1f км".format(distanceKm))
        if (minutes != null) {
            if (isNotEmpty()) append(" • ")
            append("$minutes мин")
        }
    }
}

@Composable
private fun EmptyDiaryView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Тренировок пока нет.\nДобавьте первую тренировку вручную.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
