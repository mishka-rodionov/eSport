package com.competra.center.presentation.read_card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.competra.center.data.read_card.OrientReadCardAction
import com.competra.designsystem.components.DSTextInput
import com.competra.designsystem.theme.Dimens
import com.competra.domain.models.ResultStatus
import com.competra.domain.models.orienteering.OrienteeringParticipant
import com.competra.domain.models.orienteering.OrienteeringResult
import com.competra.domain.models.orienteering.SplitTime
import com.competra.resources.R
import com.competra.utils.DateTimeFormat
import com.competra.utils.orienteering.toRaceTime
import com.competra.utils.orienteering.toSplitTime
import org.koin.compose.viewmodel.koinViewModel

/**
 * Экран для отображения данных, считанных с чипа участника соревнований по ориентированию.
 *
 * Улучшенный дизайн с использованием карточек, иконок и логического разделения блоков информации.
 *
 * @param viewModel ViewModel для управления состоянием экрана.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrientReadCardScreen(viewModel: OrientReadCardViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (state.isCompetitionFinished) {
            CompetitionFinishedView()
        } else if (state.participant == null) {
            EmptyReadCardView()
        } else {
            ReadCardContent(
                participant = state.participant!!,
                result = state.participantResult,
                rawSplits = state.rawSplits,
                groupRank = state.groupRank,
                groupTotalFinished = state.groupTotalFinished,
                expectedCpOrder = state.expectedCpNumbers,
                onEditSplit = { index -> viewModel.onAction(OrientReadCardAction.EditSplitClicked(index)) }
            )
        }

        val editingIndex = state.editingSplitIndex
        val rawSplits = state.rawSplits
        if (editingIndex != null && rawSplits != null && editingIndex in rawSplits.indices) {
            EditSplitBottomSheet(
                split = rawSplits[editingIndex],
                sheetState = sheetState,
                onDismiss = { viewModel.onAction(OrientReadCardAction.DismissEditSplit) },
                onSave = { newTimestamp ->
                    viewModel.onAction(OrientReadCardAction.SaveSplitEdit(editingIndex, newTimestamp))
                },
                onDelete = {
                    viewModel.onAction(OrientReadCardAction.DeleteSplit(editingIndex))
                }
            )
        }
    }
}

/**
 * Экран при завершённом соревновании — считывание чипов недоступно.
 */
@Composable
private fun CompetitionFinishedView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.SIZE_DOUBLE.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.play_arrow_24px),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
        Text(
            text = "Соревнование завершено",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Запись результатов недоступна после завершения соревнования.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Основное содержимое экрана при наличии данных.
 */
@Composable
private fun ReadCardContent(
    participant: OrienteeringParticipant,
    result: OrienteeringResult?,
    rawSplits: List<SplitTime>? = null,
    groupRank: Int? = null,
    groupTotalFinished: Int = 0,
    expectedCpOrder: List<Int> = emptyList(),
    onEditSplit: (index: Int) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.SIZE_BASE.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_BASE.dp)
    ) {
        // Заголовок экрана
        item {
            Text(
                text = "Результат участника",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Карточка участника
        item {
            ParticipantInfoCard(participant)
        }

        // Карточка итогового времени
        if (result != null) {
            item {
                RaceSummaryCard(participant, result, groupRank, groupTotalFinished)
            }

            // Секция сплитов
            val displaySplits = rawSplits ?: result.splits
            if (!displaySplits.isNullOrEmpty()) {
                item {
                    Text(
                        text = "Сплиты по пунктам",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                item {
                    SplitsCard(participant, displaySplits, expectedCpOrder, onEditSplit = onEditSplit)
                }
            }
        }
    }
}

/**
 * Карточка с информацией об участнике.
 */
@Composable
internal fun ParticipantInfoCard(participant: OrienteeringParticipant) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(Dimens.SIZE_BASE.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар/Иконка профиля
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_account_circle_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(Dimens.SIZE_BASE.dp))

            Column {
                Text(
                    text = "${participant.lastName} ${participant.firstName}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = participant.groupName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                if (participant.commandName.isNotEmpty()) {
                    Text(
                        text = participant.commandName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Карточка с общим временем гонки.
 */
@Composable
internal fun RaceSummaryCard(
    participant: OrienteeringParticipant,
    result: OrienteeringResult,
    groupRank: Int? = null,
    groupTotalFinished: Int = 0
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(Dimens.SIZE_BASE.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoColumn(label = "Старт", value = DateTimeFormat.transformLongToTime(participant.startTime))
                InfoColumn(label = "Финиш", value = DateTimeFormat.transformLongToTime(result.finishTime))
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = Dimens.SIZE_BASE.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ОБЩЕЕ ВРЕМЯ",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = result.totalTime?.toRaceTime() ?: "00:00:00",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (groupRank != null) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = Dimens.SIZE_BASE.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "МЕСТО В ГРУППЕ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (groupTotalFinished > 0) "$groupRank из $groupTotalFinished" else "$groupRank",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private sealed class SplitDisplayItem {
    data class Actual(val split: SplitTime, val chipIndex: Int, val isExtra: Boolean) : SplitDisplayItem()
    data class Missed(val cpNumber: Int) : SplitDisplayItem()
}

private fun buildSplitDisplayItems(
    rawSplits: List<SplitTime>,
    expectedCpOrder: List<Int>
): List<SplitDisplayItem> {
    if (expectedCpOrder.isEmpty()) {
        return rawSplits.mapIndexed { i, s -> SplitDisplayItem.Actual(s, i, isExtra = false) }
    }
    val expectedSet = expectedCpOrder.toSet()
    val shownIndices = mutableSetOf<Int>()
    val items = mutableListOf<SplitDisplayItem>()

    for (expectedCp in expectedCpOrder) {
        val visitedIdx = rawSplits.indices.firstOrNull {
            it !in shownIndices && rawSplits[it].controlPoint == expectedCp
        }
        if (visitedIdx != null) {
            // Лишние КП (не из дистанции), встретившиеся до текущего ожидаемого
            rawSplits.indices
                .filter { it < visitedIdx && it !in shownIndices && rawSplits[it].controlPoint !in expectedSet }
                .forEach { i ->
                    items.add(SplitDisplayItem.Actual(rawSplits[i], i, isExtra = true))
                    shownIndices.add(i)
                }
            items.add(SplitDisplayItem.Actual(rawSplits[visitedIdx], visitedIdx, isExtra = false))
            shownIndices.add(visitedIdx)
        } else {
            items.add(SplitDisplayItem.Missed(expectedCp))
        }
    }
    // Оставшиеся лишние КП в конце чипа
    rawSplits.indices
        .filter { it !in shownIndices && rawSplits[it].controlPoint !in expectedSet }
        .forEach { i -> items.add(SplitDisplayItem.Actual(rawSplits[i], i, isExtra = true)) }

    return items
}

/**
 * Карточка со списком сплитов.
 *
 * - Жёлтый фон: КП из чипа, которого нет в дистанции участника.
 * - Красный фон: КП дистанции, которого нет в чипе (пропущен).
 */
@Composable
internal fun SplitsCard(
    participant: OrienteeringParticipant,
    splits: List<SplitTime>,
    expectedCpOrder: List<Int> = emptyList(),
    onEditSplit: ((index: Int) -> Unit)? = null
) {
    val displayItems = remember(splits, expectedCpOrder) {
        buildSplitDisplayItems(splits, expectedCpOrder)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "КП", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                Text(text = "Сплит", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = "Время", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            displayItems.forEachIndexed { displayIndex, item ->
                // Предыдущий timestamp для расчёта сплита (ближайший Actual до текущего)
                val prevTimestamp = run {
                    for (i in displayIndex - 1 downTo 0) {
                        val prev = displayItems[i]
                        if (prev is SplitDisplayItem.Actual) return@run prev.split.timestamp
                    }
                    participant.startTime
                }

                when (item) {
                    is SplitDisplayItem.Actual -> {
                        val splitTimeStr = (item.split.timestamp - prevTimestamp).toSplitTime()
                        val totalTimeStr = if (!item.isExtra) {
                            (item.split.timestamp - participant.startTime).toSplitTime()
                        } else "—"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (item.isExtra) Color(0xFFFFEB3B).copy(alpha = 0.25f)
                                    else Color.Transparent
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val textColor = if (item.isExtra) Color(0xFF9E8000) else MaterialTheme.colorScheme.onSurface
                            Text(
                                text = item.split.controlPoint.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                color = textColor
                            )
                            Text(
                                text = splitTimeStr,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                color = if (item.isExtra) Color(0xFF9E8000) else MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = totalTimeStr,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                color = textColor
                            )
                            if (onEditSplit != null) {
                                IconButton(
                                    onClick = { onEditSplit(item.chipIndex) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.edit),
                                        contentDescription = "Редактировать КП ${item.split.controlPoint}",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    is SplitDisplayItem.Missed -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.cpNumber.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "ПРОПУЩЕН",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(2f),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (displayIndex < displayItems.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
internal fun InfoColumn(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

/**
 * Заглушка при отсутствии данных (чип не считан).
 */
@Composable
private fun EmptyReadCardView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.SIZE_DOUBLE.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.play_arrow_24px), // Используем как символ готовности/ожидания
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
        Text(
            text = "Ожидание чипа",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Приложите NFC-чип участника к устройству для считывания результатов гонки.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// region Previews

private val previewParticipant = OrienteeringParticipant(
    id = "1",
    userId = "user_1",
    firstName = "Иван",
    lastName = "Петров",
    groupId = 1L,
    groupName = "М21",
    competitionId = 1L,
    commandName = "СК Компас",
    startNumber = "101",
    startTime = 1700000000000L,
    chipNumber = "500123",
    comment = "",
    isChipGiven = true
)

private val previewSplits = listOf(
    SplitTime(controlPoint = 31, timestamp = 1700000120000L),
    SplitTime(controlPoint = 32, timestamp = 1700000300000L),
    SplitTime(controlPoint = 33, timestamp = 1700000510000L),
    SplitTime(controlPoint = 34, timestamp = 1700000780000L),
    SplitTime(controlPoint = 99, timestamp = 1700000960000L),
)

private val previewResult = OrienteeringResult(
    id = 1L,
    competitionId = 1L,
    groupId = 1L,
    participantId = "1",
    startTime = 1700000000000L,
    finishTime = 1700000960000L,
    totalTime = 960L,
    rank = 3,
    status = ResultStatus.FINISHED,
    splits = previewSplits
)

@Preview(name = "Ожидание чипа", showBackground = true)
@Composable
private fun EmptyReadCardPreview() {
    MaterialTheme {
        EmptyReadCardView()
    }
}

@Preview(name = "Соревнование завершено", showBackground = true)
@Composable
private fun CompetitionFinishedPreview() {
    MaterialTheme {
        CompetitionFinishedView()
    }
}

@Preview(name = "Результат участника", showBackground = true)
@Composable
private fun ReadCardContentPreview() {
    MaterialTheme {
        ReadCardContent(
            participant = previewParticipant,
            result = previewResult,
            rawSplits = previewSplits
        )
    }
}

@Preview(name = "Результат без сплитов", showBackground = true)
@Composable
private fun ReadCardContentNoSplitsPreview() {
    MaterialTheme {
        ReadCardContent(
            participant = previewParticipant,
            result = previewResult.copy(splits = emptyList()),
            rawSplits = emptyList()
        )
    }
}

@Preview(name = "Карточка участника", showBackground = true)
@Composable
private fun ParticipantInfoCardPreview() {
    MaterialTheme {
        ParticipantInfoCard(participant = previewParticipant)
    }
}

@Preview(name = "Карточка итогового времени", showBackground = true)
@Composable
private fun RaceSummaryCardPreview() {
    MaterialTheme {
        RaceSummaryCard(participant = previewParticipant, result = previewResult, groupRank = 2, groupTotalFinished = 5)
    }
}

@Preview(name = "Сплиты", showBackground = true)
@Composable
private fun SplitsCardPreview() {
    MaterialTheme {
        SplitsCard(
            participant = previewParticipant,
            splits = previewSplits,
            expectedCpOrder = listOf(31, 32, 33, 34, 99)
        )
    }
}

@Preview(name = "Сплиты — лишний КП + пропуск", showBackground = true)
@Composable
private fun SplitsCardDsqPreview() {
    MaterialTheme {
        SplitsCard(
            participant = previewParticipant,
            splits = listOf(
                SplitTime(controlPoint = 31, timestamp = 1700000120000L),
                SplitTime(controlPoint = 55, timestamp = 1700000200000L), // лишний
                SplitTime(controlPoint = 33, timestamp = 1700000510000L), // 32 пропущен
                SplitTime(controlPoint = 34, timestamp = 1700000780000L),
                SplitTime(controlPoint = 99, timestamp = 1700000960000L),
            ),
            expectedCpOrder = listOf(31, 32, 33, 34, 99)
        )
    }
}

// endregion

/**
 * Нижний лист для редактирования отметки на конкретном КП.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSplitBottomSheet(
    split: SplitTime,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (newTimestamp: Long) -> Unit,
    onDelete: () -> Unit
) {
    var timeStr by remember(split) {
        mutableStateOf(DateTimeFormat.transformLongToTime(split.timestamp))
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
                text = "Редактировать КП ${split.controlPoint}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = timeStr,
                onValueChanged = { timeStr = it },
                label = { Text("Время отметки (HH:mm:ss)") }
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            Button(
                onClick = {
                    val newTimestamp = DateTimeFormat.updateTimeInTimestamp(split.timestamp, timeStr)
                    if (newTimestamp != null) onSave(newTimestamp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(Dimens.SIZE_BASE.dp)
            ) {
                Text("Сохранить изменения", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                )
            ) {
                Text("Удалить отметку", fontWeight = FontWeight.Bold)
            }
        }
    }
}
