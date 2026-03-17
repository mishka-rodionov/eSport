package com.rodionov.center.presentation.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.components.clickRipple
import com.example.designsystem.theme.Dimens
import com.rodionov.domain.models.orienteering.ParticipantWithResult
import com.rodionov.resources.R
import com.rodionov.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel

/**
 * Основной экран результатов соревнований.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrienteeringCompetitionResultsScreen(
    viewModel: OrienteeringCompetitionResultsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedParticipant by remember { mutableStateOf<ParticipantWithResult?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Заголовок экрана
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SIZE_BASE.dp)
            ) {
                Text(
                    text = "Результаты",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Проверьте и утвердите итоговые протоколы",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = Dimens.SIZE_BASE.dp, vertical = Dimens.SIZE_HALF.dp),
                verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp)
            ) {
                state.groupsWithParticipantsAndResults.forEach { groupWithResults ->
                    item {
                        Text(
                            text = groupWithResults.group.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = Dimens.SIZE_HALF.dp, horizontal = 4.dp)
                        )
                    }
                    items(groupWithResults.participants) { participantWithResult ->
                        ResultParticipantCard(
                            result = participantWithResult,
                            onEditClick = {
                                selectedParticipant = participantWithResult
                                showBottomSheet = true
                            }
                        )
                    }
                }
                
                if (state.groupsWithParticipantsAndResults.isEmpty()) {
                    item {
                        EmptyResultsView()
                    }
                }
            }

            // Кнопка "Утвердить"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SIZE_BASE.dp)
            ) {
                Button(
                    onClick = { viewModel.onAction(OrienteeringCompetitionResultsViewModel.OrienteeringResultsAction.ApproveResults) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(Dimens.SIZE_BASE.dp)
                ) {
                    Text(text = "Утвердить результаты", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showBottomSheet && selectedParticipant != null) {
            EditResultBottomSheet(
                participantWithResult = selectedParticipant!!,
                sheetState = sheetState,
                onDismiss = { showBottomSheet = false },
                onSave = { startTime, finishTime ->
                    viewModel.onAction(
                        OrienteeringCompetitionResultsViewModel.OrienteeringResultsAction.UpdateResult(
                            participantWithResult = selectedParticipant!!,
                            startTime = startTime,
                            finishTime = finishTime
                        )
                    )
                    showBottomSheet = false
                }
            )
        }
    }
}

/**
 * Карточка участника с его результатом.
 */
@Composable
fun ResultParticipantCard(
    result: ParticipantWithResult,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(Dimens.SIZE_BASE.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Место или статус
            val rankText = result.result?.rank?.toString() ?: result.result?.status?.name ?: "-"
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (result.result?.rank == 1) Color(0xFFFFD700).copy(alpha = 0.2f) 
                        else MaterialTheme.colorScheme.surfaceVariant, 
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rankText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (result.result?.rank == 1) Color(0xFFB8860B) else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(Dimens.SIZE_BASE.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${result.participant.firstName} ${result.participant.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                if (result.result?.isEdited == true) {
                    Text(
                        text = "Отредактировано вручную",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }

            // Время
            Text(
                text = DateTimeFormat.transformLongToTime(result.result?.totalTime?.let { it * 1000 }),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (result.result?.isEditable == true) {
                Spacer(modifier = Modifier.width(Dimens.SIZE_HALF.dp))
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.edit),
                        contentDescription = "Edit result",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyResultsView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.SIZE_DOUBLE.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_location_on_24px),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
        Text(
            text = "Результаты еще не загружены или соревнование не завершено",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Боттомшит для редактирования времени старта и финиша.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditResultBottomSheet(
    participantWithResult: ParticipantWithResult,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (startTime: Long?, finishTime: Long?) -> Unit
) {
    var startTimeStr by remember {
        mutableStateOf(DateTimeFormat.transformLongToTime(participantWithResult.result?.startTime) ?: "")
    }
    var finishTimeStr by remember {
        mutableStateOf(DateTimeFormat.transformLongToTime(participantWithResult.result?.finishTime) ?: "")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SIZE_BASE.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Корректировка времени",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "${participantWithResult.participant.firstName} ${participantWithResult.participant.lastName}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = Dimens.SIZE_HALF.dp)
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = startTimeStr,
                onValueChanged = { startTimeStr = it },
                label = { Text("Время старта (HH:mm:ss)") }
            )
            
            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = finishTimeStr,
                onValueChanged = { finishTimeStr = it },
                label = { Text("Время финиша (HH:mm:ss)") }
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

            Button(
                onClick = {
                    val newStart = DateTimeFormat.updateTimeInTimestamp(participantWithResult.result?.startTime, startTimeStr)
                    val newFinish = DateTimeFormat.updateTimeInTimestamp(participantWithResult.result?.finishTime, finishTimeStr)
                    onSave(newStart, newFinish)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(Dimens.SIZE_BASE.dp)
            ) {
                Text("Сохранить изменения", fontWeight = FontWeight.Bold)
            }
        }
    }
}
