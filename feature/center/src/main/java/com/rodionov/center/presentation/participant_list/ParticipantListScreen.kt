package com.rodionov.center.presentation.participant_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rodionov.center.data.participant_list.ParticipantListState
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.ParticipantGroupParticipants
import kotlinx.coroutines.launch

@Composable
fun ParticipantListScreen(
    viewModel: ParticipantListViewModel = viewModel(),
    competitionId: Long
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = competitionId) {
        viewModel.getCompetitionDetails(competitionId)
    }
    ParticipantListContent(state = state)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParticipantListContent(
    state: ParticipantListState
) {
    val pagerState = rememberPagerState() { state.participantGroupWithParticipants.size }
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

        HorizontalPager(
            state = pagerState
        ) { page ->
            ParticipantList(participants = state.participantGroupWithParticipants[page].participants)
        }
    }
}

@Composable
fun ParticipantList(participants: List<OrienteeringParticipant>) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(participants) { participant ->
            ParticipantItem(participant = participant)
        }
    }
}

@Composable
fun ParticipantItem(participant: OrienteeringParticipant) {
    Text(text = "${participant.firstName} ${participant.lastName}")
}

@Preview
@Composable
fun ParticipantListScreenPreview() {
    ParticipantListContent(
        state = ParticipantListState(
            participantGroupWithParticipants = listOf(
                ParticipantGroupParticipants(
                    group = ParticipantGroup(
                        groupId = 1L,
                        competitionId = 1L,
                        title = "M21",
                        distance = 10.0,
                        countOfControls = 20,
                        maxTimeInMinute = 120
                    ),
                    participants = listOf(
                        OrienteeringParticipant(
                            id = 1,
                            userId = "1",
                            firstName = "John",
                            lastName = "Doe",
                            groupId = 1,
                            commandName = "Command 1",
                            startNumber = "1",
                            chipNumber = "12345",
                            comment = "Comment 1"
                        ),
                        OrienteeringParticipant(
                            id = 2,
                            userId = "2",
                            firstName = "Jane",
                            lastName = "Doe",
                            groupId = 1,
                            commandName = "Command 1",
                            startNumber = "2",
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
                        maxTimeInMinute = 100
                    ),
                    participants = listOf(
                        OrienteeringParticipant(
                            id = 3,
                            userId = "3",
                            firstName = "Peter",
                            lastName = "Jones",
                            groupId = 2,
                            commandName = "Command 2",
                            startNumber = "3",
                            chipNumber = "67890",
                            comment = "Comment 3"
                        )
                    )
                )
            )
        )
    )
}
