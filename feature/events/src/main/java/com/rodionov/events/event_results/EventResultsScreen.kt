package com.rodionov.events.event_results

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodionov.domain.models.ResultStatus
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.models.orienteering.ParticipantWithResult
import com.rodionov.utils.DateTimeFormat
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

/**
 * Экран результатов события.
 * Отображает результаты участников, сгруппированные по категориям (табам).
 *
 * @param eventId Идентификатор события.
 * @param viewModel Вьюмодель экрана.
 */
@Composable
fun EventResultsScreen(
    eventId: Long,
    viewModel: EventResultsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.loadResults(eventId)
    }

    if (state.groupsWithResults.isNotEmpty()) {
        val groups = state.groupsWithResults
        val pagerState = rememberPagerState(pageCount = { groups.size })
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize()) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                groups.forEachIndexed { index, groupWithResults ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(text = groupWithResults.group.title)
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                ResultsList(participants = groups[page].participants)
            }
        }
    }
}

/**
 * Список результатов для конкретной группы.
 *
 * @param participants Список участников с их результатами.
 */
@Composable
private fun ResultsList(participants: List<ParticipantWithResult>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            ResultsHeader()
        }
        items(participants) { item ->
            ResultItem(item = item)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

/**
 * Заголовок таблицы результатов.
 */
@Composable
private fun ResultsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(text = "№", modifier = Modifier.weight(0.1f), fontWeight = FontWeight.Bold)
        Text(text = "Участник", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
        Text(text = "Результат", modifier = Modifier.weight(0.3f), fontWeight = FontWeight.Bold)
        Text(text = "Место", modifier = Modifier.weight(0.1f), fontWeight = FontWeight.Bold)
    }
}

/**
 * Строка результата участника.
 *
 * @param item Данные участника и его результата.
 */
@Composable
private fun ResultItem(item: ParticipantWithResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = item.participant.startNumber,
            modifier = Modifier.weight(0.1f)
        )
        Text(
            text = "${item.participant.lastName} ${item.participant.firstName}",
            modifier = Modifier.weight(0.5f)
        )
        Text(
            text = formatResult(item.result),
            modifier = Modifier.weight(0.3f)
        )
        Text(
            text = item.result?.rank?.toString() ?: "-",
            modifier = Modifier.weight(0.1f)
        )
    }
}

/**
 * Форматирует время результата или статус.
 */
private fun formatResult(result: OrienteeringResult?): String {
    if (result == null) return "-"
    return when (result.status) {
        ResultStatus.FINISHED -> DateTimeFormat.transformLongToTime(result.totalTime?.let { it * 1000 })
        else -> result.status.name
    }
}

@Preview(showBackground = true)
@Composable
private fun EventResultsPreview() {
    val mockParticipants = listOf(
        ParticipantWithResult(
            participant = OrienteeringParticipant(
                id = 1,
                userId = "user_1",
                firstName = "Иван",
                lastName = "Иванов",
                groupId = 1,
                groupName = "М21",
                competitionId = 1,
                commandName = "Команда А",
                startNumber = "101",
                startTime = 0L,
                chipNumber = "12345",
                comment = "",
                isChipGiven = true
            ),
            result = OrienteeringResult(
                id = 1,
                competitionId = 1,
                groupId = 1,
                participantId = 1,
                totalTime = 1800L,
                rank = 1,
                status = ResultStatus.REGISTERED
            )
        ),
        ParticipantWithResult(
            participant = OrienteeringParticipant(
                id = 2,
                userId = "user_2",
                firstName = "Петр",
                lastName = "Петров",
                groupId = 1,
                groupName = "М21",
                competitionId = 1,
                commandName = "Команда Б",
                startNumber = "102",
                startTime = 0L,
                chipNumber = "54321",
                comment = "",
                isChipGiven = true
            ),
            result = OrienteeringResult(
                id = 2,
                competitionId = 1,
                groupId = 1,
                participantId = 2,
                totalTime = 1950L,
                rank = 2,
                status = ResultStatus.FINISHED
            )
        )
    )
    MaterialTheme {
        ResultsList(participants = mockParticipants)
    }
}
