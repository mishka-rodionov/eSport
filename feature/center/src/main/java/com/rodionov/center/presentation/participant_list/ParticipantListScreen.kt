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
                        .sortedBy { it.startNumber.toIntOrNull() ?: Int.MAX_VALUE }

                    if (participants.isEmpty()) {
                        EmptyParticipantsView(message = "В этой группе пока нет участников")
                    } else {
                        ParticipantList(
                            participants = participants,
                            onEditClick = { participant ->
                                userAction.invoke(ParticipantListAction.ShowEditParticipantDialog(page, participant))
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
                        userAction.invoke(
                            ParticipantListAction.UpdateParticipant(
                                participant = editingParticipant.copy(
                                    firstName = firstName,
                                    lastName = secondName
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

@Composable
fun ParticipantList(
    participants: List<OrienteeringParticipant>,
    onEditClick: (OrienteeringParticipant) -> Unit
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
                onEditClick = { onEditClick(participant) }
            )
        }
    }
}

@Composable
fun ParticipantCard(
    participant: OrienteeringParticipant,
    displayIndex: Int,
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
            // Индекс или номер участника
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
        }
    }
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
