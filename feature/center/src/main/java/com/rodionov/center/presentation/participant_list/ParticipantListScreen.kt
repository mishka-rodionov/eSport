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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
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
import com.rodionov.ui.BaseAction
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

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
            userAction,
            state.group,
            state.participantGroupWithParticipants[state.group].group.title
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
                ParticipantList(participants = state.participantGroupWithParticipants[page].participants.sortedBy { it.startNumber.toIntOrNull() })
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
fun CreateParticipantDialog(userAction: (BaseAction) -> Unit, group: Int, groupName: String) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    DSBottomDialog(
        sheetState = sheetState,
        sheetContent = { CreateParticipantDialogContent(userAction, group, groupName) },
        onDismiss = { userAction.invoke(ParticipantListAction.HideCreateParticipantDialog) },
    )

}

@Composable
fun CreateParticipantDialogContent(
    userAction: (BaseAction) -> Unit,
    group: Int,
    groupName: String
) {
    var firstName by remember { mutableStateOf("") }
    var secondName by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(all = Dimens.SIZE_HALF.dp)) {

        Text(text = "Группа $groupName", modifier = Modifier.padding(bottom = Dimens.SIZE_BASE.dp))

        DSTextInput(
            modifier = Modifier.fillMaxWidth(),
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
                .padding(top = Dimens.SIZE_BASE.dp), onClick = {
                if (firstName.isNotEmpty() && secondName.isNotEmpty()) {
                    userAction.invoke(
                        ParticipantListAction.CreateNewParticipant(
                            group = group,
                            firstName = firstName,
                            secondName = secondName
                        )
                    )
                    firstName = ""
                    secondName = ""
                } else {
                    //здесь должна быть ошибка об обязательности заполнения полей
                }
            }) {
            Text(text = "Сохранить участника")
        }
    }
}

@Composable
fun ParticipantList(participants: List<OrienteeringParticipant>) {
    LazyColumn(modifier = Modifier.fillMaxHeight().padding(16.dp)) {
        items(participants) { participant ->
            ParticipantItem(participant = participant)
        }
    }
}

@Composable
fun ParticipantItem(participant: OrienteeringParticipant) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SIZE_TWO.dp)
        ) {
            Text(
                text = participant.startNumber,
                modifier = Modifier.weight(0.1F)
            )
            Text(
                text = "${participant.firstName} ${participant.lastName}",
                modifier = Modifier.weight(0.9F)
            )
            Text(
                text = participant.startTime.toString(),
                modifier = Modifier.weight(0.2F)
            )
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
                            competitionId = 1,
                            commandName = "Command 1",
                            startNumber = "1",
                            startTime = 10L,
                            chipNumber = "12345",
                            comment = "Comment 1"
                        ),
                        OrienteeringParticipant(
                            id = 2,
                            userId = "2",
                            firstName = "Jane",
                            lastName = "Doe",
                            groupId = 1,
                            competitionId = 1,
                            commandName = "Command 1",
                            startNumber = "2",
                            startTime = 20L,
                            chipNumber = "54321",
                            comment = "Comment 2"
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
                            competitionId = 1,
                            commandName = "Command 2",
                            startNumber = "3",
                            startTime = 30L,
                            chipNumber = "67890",
                            comment = "Comment 3"
                        )
                    )
                )
            )
        )
    )
}
