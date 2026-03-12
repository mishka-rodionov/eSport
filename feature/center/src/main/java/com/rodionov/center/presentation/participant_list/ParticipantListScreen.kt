package com.rodionov.center.presentation.participant_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.colors.LightColors
import com.example.designsystem.components.DSBottomDialog
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.participant_list.ParticipantListAction
import com.rodionov.center.data.participant_list.ParticipantListState
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
 *
 * Основные возможности:
 * - Отображение вкладок для каждой [ParticipantGroup] с помощью [TabRow] и [HorizontalPager].
 * - Просмотр списка участников внутри выбранной группы, отсортированных по стартовому номеру.
 * - Создание новых участников через плавающую кнопку действия (FAB).
 * - Редактирование данных существующих участников через модальное диалоговое окно.
 * - Автоматическая загрузка данных о соревновании при запуске экрана.
 *
 * @param viewModel ViewModel для управления состоянием и бизнес-логикой экрана.
 * По умолчанию используется [ParticipantListViewModel], внедряемая через Koin.
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
    ParticipantListContent(userAction = userAction, state = state)
    if (state.isShowParticipantCreateDialog) {
        CreateParticipantDialog(
            userAction = userAction,
            group = state.group,
            groupName = state.participantGroupWithParticipants[state.group].group.title,
            editingParticipant = state.editingParticipant
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParticipantListContent(
    userAction: (BaseAction) -> Unit,
    state: ParticipantListState
) {
    val pagerState = rememberPagerState { state.participantGroupWithParticipants.size }
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            state.participantGroupWithParticipants.forEachIndexed { index, group ->
                Tab(
                    text = { Text(text = group.group.title) },
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState
            ) { page ->
                ParticipantList(
                    participants = state.participantGroupWithParticipants[page].participants.sortedBy { it.startNumber.toIntOrNull() },
                    onEditClick = { participant ->
                        userAction.invoke(ParticipantListAction.ShowEditParticipantDialog(page, participant))
                    }
                )
            }

            FloatingActionButton(
                onClick = {
                    userAction.invoke(ParticipantListAction.ShowCreateParticipantDialog(pagerState.currentPage))
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = Dimens.SIZE_BASE.dp, end = Dimens.SIZE_BASE.dp)
            ) {
                Icon(
                    painter = painterResource(com.example.designsystem.R.drawable.ic_add),
                    contentDescription = null,
                )
            }
        }
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

/**
 * Контент диалога создания/редактирования участника.
 * При создании нового участника диалог остается открытым для ввода следующего,
 * переводя фокус на поле имени.
 */
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

    // Запрашиваем фокус при открытии диалога
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = Modifier.padding(all = Dimens.SIZE_HALF.dp)) {

        Text(text = "Группа $groupName", modifier = Modifier.padding(bottom = Dimens.SIZE_BASE.dp))

        DSTextInput(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = {
                Text(text = "Имя")
            },
//            isError = state.errors.isGroupTitleError,
            text = firstName,
            onValueChanged = {
                firstName = it
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Фамилия")
            },
//            isError = state.errors.isGroupTitleError,
            text = secondName,
            onValueChanged = {
                secondName = it
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.SIZE_BASE.dp),
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
                        // Очищаем поля и возвращаем фокус для ввода следующего участника
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
                        // При редактировании диалог закроется через ViewModel (Action.HideCreateParticipantDialog)
                    }
                } else {
                    //здесь должна быть ошибка об обязательности заполнения полей
                }
            }) {
            Text(text = if (editingParticipant == null) "Сохранить участника" else "Изменить")
        }
    }
}

@Composable
fun ParticipantList(
    participants: List<OrienteeringParticipant>,
    onEditClick: (OrienteeringParticipant) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxHeight().padding(16.dp)) {
        itemsIndexed(participants) { index, participant ->
            ParticipantItem(
                participant = participant,
                index = index + 1,
                onEditClick = { onEditClick(participant) }
            )
        }
    }
}

@Composable
fun ParticipantItem(participant: OrienteeringParticipant, index: Int, onEditClick: () -> Unit) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SIZE_TWO.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = index.toString(),
                modifier = Modifier.weight(0.1F)
            )
            Text(
                text = "${participant.firstName} ${participant.lastName}",
                modifier = Modifier.weight(0.8F)
            )
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.edit),
                    contentDescription = "edit participant",
                    tint = LightColors.greyB8
                )
            }
//            Text(
//                text = participant.startTime.toString(),
//                modifier = Modifier.weight(0.2F)
//            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = Dimens.SIZE_HALF.dp),
            thickness = Dimens.SIZE_SINGLE.dp,
            color = LightColors.greyB8
        )
    }
}

@Preview
@Composable
fun ParticipantListScreenPreview() {
    ParticipantListContent(
        userAction = {},
        state = ParticipantListState(
            participantGroupWithParticipants = listOf(
                ParticipantGroupParticipants(
                    group = ParticipantGroup(
                        groupId = 1L,
                        competitionId = 1L,
                        title = "M21",
                        distance = 10.0,
                        countOfControls = 20,
                        maxTimeInMinute = 120,
                        controlPoints = emptyList()
                    ),
                    participants = listOf(
                        OrienteeringParticipant(
                            id = 1,
                            userId = "1",
                            firstName = "John",
                            lastName = "Doe",
                            groupId = 1,
                            groupName = "M21",
                            competitionId = 1,
                            commandName = "Command 1",
                            startNumber = "1",
                            startTime = 10L,
                            chipNumber = "12345",
                            comment = "Comment 1",
                            isChipGiven = false
                        ),
                        OrienteeringParticipant(
                            id = 2,
                            userId = "2",
                            firstName = "Jane",
                            lastName = "Doe",
                            groupId = 1,
                            groupName = "M21",
                            competitionId = 1,
                            commandName = "Command 1",
                            startNumber = "2",
                            startTime = 20L,
                            chipNumber = "54321",
                            comment = "Comment 2",
                            isChipGiven = false
                        )
                    )
                ),
                ParticipantGroupParticipants(
                    group = ParticipantGroup(
                        groupId = 2L,
                        competitionId = 1L,
                        title = "W21",
                        distance = 8.0,
                        countOfControls = 15,
                        maxTimeInMinute = 100,
                        controlPoints = emptyList()
                    ),
                    participants = listOf(
                        OrienteeringParticipant(
                            id = 3,
                            userId = "3",
                            firstName = "Peter",
                            lastName = "Jones",
                            groupId = 2,
                            groupName = "W21",
                            competitionId = 1,
                            commandName = "Command 2",
                            startNumber = "3",
                            startTime = 30L,
                            chipNumber = "67890",
                            comment = "Comment 3",
                            isChipGiven = false
                        )
                    )
                )
            )
        )
    )
}
