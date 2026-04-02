package com.rodionov.center.presentation.participant_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.components.DSBottomDialog
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.participant_list.ParticipantListAction
import com.rodionov.center.data.participant_list.ParticipantListState
import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.ParticipantGroupParticipants
import com.rodionov.resources.R
import com.rodionov.ui.BaseAction
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

/**
 * Экран отображения и управления списком участников соревнований,
 * разделенный на вкладки по группам участников.
 */
@Composable
fun ParticipantListScreen(
    viewModel: ParticipantListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val userAction = remember { viewModel::onAction }

    LaunchedEffect(viewModel) {
        viewModel.getCompetitionDetails()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        ParticipantListContent(userAction = userAction, state = state)
    }

    if (state.isShowParticipantCreateDialog) {
        CreateParticipantDialog(
            userAction = userAction,
            group = state.group,
            groupName = state.participantGroupWithParticipants.getOrNull(state.group)?.group?.title ?: "",
            editingParticipant = state.editingParticipant
        )
    }

    state.deletingParticipant?.let { participant ->
        DeleteParticipantDialog(
            participant = participant,
            onDismiss = { userAction.invoke(ParticipantListAction.HideDeleteParticipantDialog) },
            onConfirm = { userAction.invoke(ParticipantListAction.DeleteParticipant(participant)) }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ParticipantListContent(
    userAction: (BaseAction) -> Unit,
    state: ParticipantListState
) {
    val pagerState = rememberPagerState { state.participantGroupWithParticipants.size }
    val scope = rememberCoroutineScope()
    
    Column(modifier = Modifier.fillMaxSize()) {
        if (state.participantGroupWithParticipants.isNotEmpty()) {
            SecondaryScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {},
                indicator = { 
//                    if (pagerState.currentPage < tabPositions.size) {
//                        TabRowDefaults.SecondaryIndicator(
//                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
                }
            ) {
                state.participantGroupWithParticipants.forEachIndexed { index, group ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                text = group.group.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (state.participantGroupWithParticipants.isEmpty()) {
                EmptyParticipantsView(message = "Группы участников не найдены")
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val participants = state.participantGroupWithParticipants[page].participants
                        .sortedWith(
                            compareBy(
                                { if (isValidTimestamp(it.startTime)) it.startTime else Long.MAX_VALUE },
                                { it.startNumber.toIntOrNull() ?: Int.MAX_VALUE }
                            )
                        )

                    if (participants.isEmpty()) {
                        EmptyParticipantsView(message = "В этой группе пока нет участников")
                    } else {
                        ParticipantList(
                            participants = participants,
                            onEditClick = { participant ->
                                userAction.invoke(ParticipantListAction.ShowEditParticipantDialog(page, participant))
                            },
                            onDeleteClick = { participant ->
                                userAction.invoke(ParticipantListAction.ShowDeleteParticipantDialog(participant))
                            }
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    if (state.participantGroupWithParticipants.isNotEmpty()) {
                        userAction.invoke(ParticipantListAction.ShowCreateParticipantDialog(pagerState.currentPage))
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Dimens.SIZE_BASE.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(Dimens.SIZE_BASE.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_add_24px),
                    contentDescription = "Add participant"
                )
            }
        }
    }
}

@Composable
private fun EmptyParticipantsView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.SIZE_DOUBLE.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_location_on_24px),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateParticipantDialog(
    userAction: (BaseAction) -> Unit,
    group: Int,
    groupName: String,
    editingParticipant: OrienteeringParticipant?
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    DSBottomDialog(
        sheetState = sheetState,
        sheetContent = {
            CreateParticipantDialogContent(
                userAction = userAction,
                group = group,
                groupName = groupName,
                editingParticipant = editingParticipant
            )
        },
        onDismiss = { userAction.invoke(ParticipantListAction.HideCreateParticipantDialog) },
    )
}

@Composable
fun CreateParticipantDialogContent(
    userAction: (BaseAction) -> Unit,
    group: Int,
    groupName: String,
    editingParticipant: OrienteeringParticipant?
) {
    var firstName by remember(editingParticipant) { mutableStateOf(editingParticipant?.firstName ?: "") }
    var secondName by remember(editingParticipant) { mutableStateOf(editingParticipant?.lastName ?: "") }
    var startTimeInput by remember(editingParticipant) {
        mutableStateOf(
            if (editingParticipant != null && isValidTimestamp(editingParticipant.startTime))
                formatStartTime(editingParticipant.startTime)
            else ""
        )
    }
    var startTimeError by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .padding(Dimens.SIZE_BASE.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = if (editingParticipant == null) "Новый участник" else "Редактирование",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Группа: $groupName",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = Dimens.SIZE_HALF.dp)
        )

        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

        DSTextInput(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = { Text(text = "Имя") },
            text = firstName,
            onValueChanged = { firstName = it },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Фамилия") },
            text = secondName,
            onValueChanged = { secondName = it },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        if (editingParticipant != null) {
            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Стартовое время (ЧЧ:ММ или ЧЧ:ММ:СС или ММ:СС)") },
                text = startTimeInput,
                onValueChanged = {
                    startTimeInput = it
                    startTimeError = false
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = startTimeError
            )

            if (startTimeError) {
                Text(
                    text = "Введите время в формате ЧЧ:ММ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = Dimens.SIZE_HALF.dp, top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
            onClick = {
                if (firstName.isNotEmpty() && secondName.isNotEmpty()) {
                    if (editingParticipant == null) {
                        userAction.invoke(
                            ParticipantListAction.CreateNewParticipant(
                                group = group,
                                firstName = firstName,
                                secondName = secondName
                            )
                        )
                        firstName = ""
                        secondName = ""
                        focusRequester.requestFocus()
                    } else {
                        val parsedStartTime = if (startTimeInput.isNotEmpty()) {
                            parseTimeInput(startTimeInput, editingParticipant.startTime)
                        } else {
                            editingParticipant.startTime
                        }
                        if (startTimeInput.isNotEmpty() && parsedStartTime == null) {
                            startTimeError = true
                            return@Button
                        }
                        userAction.invoke(
                            ParticipantListAction.UpdateParticipant(
                                participant = editingParticipant.copy(
                                    firstName = firstName,
                                    lastName = secondName,
                                    startTime = parsedStartTime ?: editingParticipant.startTime
                                )
                            )
                        )
                    }
                }
            }
        ) {
            Text(
                text = if (editingParticipant == null) "Добавить участника" else "Сохранить изменения",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteParticipantDialog(
    participant: OrienteeringParticipant,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    DSBottomDialog(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .padding(Dimens.SIZE_BASE.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Удалить участника?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                Text(
                    text = "${participant.firstName} ${participant.lastName} (№${participant.startNumber})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    onClick = onConfirm
                ) {
                    Text(text = "Удалить", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
                    onClick = onDismiss
                ) {
                    Text(text = "Отмена", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
            }
        },
        onDismiss = onDismiss
    )
}

@Composable
fun ParticipantList(
    participants: List<OrienteeringParticipant>,
    onEditClick: (OrienteeringParticipant) -> Unit,
    onDeleteClick: (OrienteeringParticipant) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.SIZE_BASE.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp)
    ) {
        itemsIndexed(participants) { index, participant ->
            ParticipantCard(
                participant = participant,
                displayIndex = index + 1,
                onEditClick = { onEditClick(participant) },
                onDeleteClick = { onDeleteClick(participant) }
            )
        }
    }
}

@Composable
fun ParticipantCard(
    participant: OrienteeringParticipant,
    displayIndex: Int,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val startTimeText = if (isValidTimestamp(participant.startTime)) formatStartTime(participant.startTime) else "—"

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
            // Номер участника
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = participant.startNumber.ifEmpty { displayIndex.toString() },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(Dimens.SIZE_BASE.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${participant.firstName} ${participant.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                if (participant.commandName.isNotEmpty()) {
                    Text(
                        text = participant.commandName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Старт: $startTimeText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.edit),
                    contentDescription = "Edit participant",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(Dimens.SIZE_HALF.dp))

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f), CircleShape)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.delete),
                    contentDescription = "Delete participant",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/** Минимальный допустимый timestamp — 1 января 2000 года. */
private const val MIN_VALID_TIMESTAMP_MS = 946_684_800_000L

/** Возвращает true, если [ms] — это реальная дата (не плейсхолдер и не отрицательное значение). */
private fun isValidTimestamp(ms: Long): Boolean = ms >= MIN_VALID_TIMESTAMP_MS

private fun formatStartTime(startTimeMs: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = startTimeMs
    val h = calendar.get(Calendar.HOUR_OF_DAY)
    val m = calendar.get(Calendar.MINUTE)
    val s = calendar.get(Calendar.SECOND)
    return if (s > 0) {
        String.format("%02d:%02d:%02d", h, m, s)
    } else {
        String.format("%02d:%02d", h, m)
    }
}

/**
 * Парсит строку времени в Unix timestamp (мс).
 *
 * Поддерживаемые форматы:
 * - "ЧЧ:ММ"      (2 части, первая ≤ 23) → часы:минуты
 * - "ММ:СС"      (2 части, первая > 23) → минуты:секунды
 * - "ЧЧ:ММ:СС"  (3 части)              → часы:минуты:секунды
 *
 * Дата берётся из [baseDateMs] если это реальная дата, иначе — текущая.
 * Возвращает null при неверном формате.
 */
private fun parseTimeInput(timeStr: String, baseDateMs: Long): Long? {
    val parts = timeStr.trim().split(":")
    val hours: Int
    val minutes: Int
    val seconds: Int

    when (parts.size) {
        2 -> {
            val first = parts[0].toIntOrNull() ?: return null
            val second = parts[1].toIntOrNull() ?: return null
            if (second !in 0..59) return null
            if (first > 23) {
                // Интерпретируем как ММ:СС
                if (first > 59) return null
                hours = 0
                minutes = first
                seconds = second
            } else {
                // Интерпретируем как ЧЧ:ММ
                hours = first
                minutes = second
                seconds = 0
            }
        }
        3 -> {
            hours = parts[0].toIntOrNull() ?: return null
            minutes = parts[1].toIntOrNull() ?: return null
            seconds = parts[2].toIntOrNull() ?: return null
            if (hours !in 0..23 || minutes !in 0..59 || seconds !in 0..59) return null
        }
        else -> return null
    }

    val calendar = Calendar.getInstance()
    // Используем дату из baseDateMs только если это реальная дата, иначе — сегодня
    if (isValidTimestamp(baseDateMs)) {
        calendar.timeInMillis = baseDateMs
    }
    calendar.set(Calendar.HOUR_OF_DAY, hours)
    calendar.set(Calendar.MINUTE, minutes)
    calendar.set(Calendar.SECOND, seconds)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

@Preview(showBackground = true)
@Composable
fun ParticipantListScreenPreview() {
    MaterialTheme {
        ParticipantListContent(
            userAction = {},
            state = ParticipantListState(
                participantGroupWithParticipants = listOf(
                    ParticipantGroupParticipants(
                        group = ParticipantGroup(
                            groupId = 1L,
                            competitionId = 1L,
                            title = "M21",
                            gender = Gender.MALE,
                            minAge = 21,
                            maxAge = null,
                            distanceId = 1L,
                            maxParticipants = 100,
                            isSynced = true,
                            lastModified = System.currentTimeMillis()
                        ),
                        participants = listOf(
                            OrienteeringParticipant(
                                id = 1,
                                userId = "1",
                                firstName = "Иван",
                                lastName = "Иванов",
                                groupId = 1,
                                groupName = "M21",
                                competitionId = 1,
                                commandName = "Зенит",
                                startNumber = "1",
                                startTime = 10L,
                                chipNumber = "12345",
                                comment = "",
                                isChipGiven = false
                            )
                        )
                    )
                )
            )
        )
    }
}
