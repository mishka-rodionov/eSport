package com.rodionov.center.presentation.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.colors.LightColors
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
    val sheetState = rememberModalBottomSheetState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.SIZE_TWO.dp),
            ) {
                // Временное решение: отображаем участников первой группы
                state.groupsWithParticipantsAndResults.forEach { groupWithResults ->
                    item {
                        Text(
                            text = groupWithResults.group.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = Dimens.SIZE_SINGLE.dp)
                        )
                    }
                    items(groupWithResults.participants) { participantWithResult ->
                        ParticipantItem(
                            result = participantWithResult,
                            onEditClick = {
                                selectedParticipant = participantWithResult
                                showBottomSheet = true
                            }
                        )
                    }
                }
            }

            // Кнопка "Утвердить" внизу списка
            Button(
                onClick = { viewModel.onAction(OrienteeringCompetitionResultsViewModel.OrienteeringResultsAction.ApproveResults) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SIZE_TWO.dp)
            ) {
                Text(text = "Утвердить")
            }
        }

        // Боттомшит для редактирования результатов
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
 * Элемент списка участников с информацией о результате.
 *
 * @param result Данные участника и его результата.
 * @param onEditClick Коллбэк при нажатии на иконку редактирования.
 */
@Composable
fun ParticipantItem(
    result: ParticipantWithResult,
    onEditClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SIZE_TWO.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = result.result?.rank?.toString() ?: result.result?.status?.name ?: "UNK",
                modifier = Modifier.weight(0.1F)
            )
            Column(modifier = Modifier.weight(0.6F)) {
                Text(
                    text = "${result.participant.firstName} ${result.participant.lastName}",
                    fontWeight = FontWeight.Medium
                )
                if (result.result?.isEdited == true) {
                    Text(
                        text = "Отредактировано",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Text(
                text = DateTimeFormat.transformLongToTime(result.result?.totalTime?.let { it * 1000 }),
                modifier = Modifier.weight(0.2F)
            )

            // Отображаем иконку редактирования, если результат можно редактировать
            if (result.result?.isEditable == true) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.edit),
                        contentDescription = "result_edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(0.1f))
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = Dimens.SIZE_HALF.dp),
            thickness = Dimens.SIZE_SINGLE.dp,
            color = LightColors.greyB8
        )
    }
}

/**
 * Боттомшит для редактирования времени старта и финиша участника.
 *
 * @param participantWithResult Данные участника.
 * @param sheetState Состояние BottomSheet.
 * @param onDismiss Коллбэк для закрытия диалога.
 * @param onSave Коллбэк для сохранения изменений.
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
        mutableStateOf(DateTimeFormat.transformLongToTime(participantWithResult.result?.startTime))
    }
    var finishTimeStr by remember {
        mutableStateOf(DateTimeFormat.transformLongToTime(participantWithResult.result?.finishTime))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.SIZE_TWO.dp)
                .padding(bottom = Dimens.SIZE_QUARTER.dp)
        ) {
            Text(
                text = "Редактирование результата",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Dimens.SIZE_TWO.dp))
            Text(
                text = "Участник: ${participantWithResult.participant.firstName} ${participantWithResult.participant.lastName}",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(Dimens.SIZE_TWO.dp))

            OutlinedTextField(
                value = startTimeStr,
                onValueChange = { startTimeStr = it },
                label = { Text("Время старта (HH:mm)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(Dimens.SIZE_SINGLE.dp))
            OutlinedTextField(
                value = finishTimeStr,
                onValueChange = { finishTimeStr = it },
                label = { Text("Время финиша (HH:mm)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_THREE.dp))

            Button(
                onClick = {
                    val newStart = DateTimeFormat.updateTimeInTimestamp(participantWithResult.result?.startTime, startTimeStr)
                    val newFinish = DateTimeFormat.updateTimeInTimestamp(participantWithResult.result?.finishTime, finishTimeStr)
                    onSave(newStart, newFinish)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }
    }
}
