package com.rodionov.events.presentation.eventDetails

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodionov.domain.models.cyclic_event.CyclicEventDetails
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.domain.models.events.EventStatus
import com.rodionov.domain.models.events.EventType
import com.rodionov.resources.R
import com.rodionov.events.data.details.EventDetailsState
import com.rodionov.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel

/**
 * Экран деталей события.
 * @param idEvent Идентификатор события.
 * @param viewModel Вьюмодель экрана.
 */
@Composable
fun EventDetailsScreen(
    idEvent: Long,
    viewModel: EventDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(idEvent) {
        viewModel.initialize(idEvent)
    }

    ScrollableColumnScreenWithImageAnimation(
        state = state,
        onAction = viewModel::onAction
    )
}

/**
 * Основной контент экрана деталей события с анимацией изображения при прокрутке.
 * @param state Состояние экрана.
 * @param onAction Обработчик действий.
 */
@Composable
fun ScrollableColumnScreenWithImageAnimation(
    state: EventDetailsState,
    onAction: (EventDetailsAction) -> Unit
) {
    val scrollState = rememberScrollState()
    val imageHeightPx = with(LocalDensity.current) { 250.dp.toPx() }

    // Анимация прозрачности изображения
    val imageAlpha by animateFloatAsState(
        targetValue = (1f - (scrollState.value / imageHeightPx)).coerceIn(0f, 1f),
        label = "Image Alpha Animation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = state.eventDetails?.title ?: "",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.forest),
            contentDescription = "Header Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .graphicsLayer {
                    alpha = imageAlpha
                },
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = state.eventDetails?.city ?: "", modifier = Modifier.weight(1f))
            Text(
                text = DateTimeFormat.transformLongToDisplayDate(state.eventDetails?.startDate),
                modifier = Modifier.weight(1f)
            )
        }

        // Логика отображения кнопок в зависимости от статуса события
        EventActionButtons(
            status = state.eventDetails?.status,
            onAction = onAction
        )

        Text(
            text = state.eventDetails?.description ?: "",
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        state.eventDetails?.participantGroups?.let { groups ->
            ParticipantGroupsList(groups = groups, onGroupClick = { group ->
                onAction(EventDetailsAction.OnGroupClick(group))
            })
        }
    }
}

/**
 * Отображает кнопки действия в зависимости от статуса события.
 * @param status Статус события.
 * @param onAction Обработчик действий.
 */
@Composable
private fun EventActionButtons(
    status: EventStatus?,
    onAction: (EventDetailsAction) -> Unit
) {
    when (status) {
        EventStatus.CREATED, EventStatus.REGISTRATION -> {
            Button(
                onClick = { /* TODO: Регистрация */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Зарегистрироваться")
            }
        }

        EventStatus.FINISHED -> {
            Button(
                onClick = { onAction(EventDetailsAction.ToResults) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Результаты")
            }
        }

        else -> {
            // Для других статусов кнопки не отображаем или добавляем иную логику
        }
    }
}

/**
 * Блок с отображением списка групп участников.
 * @param groups Список групп.
 * @param onGroupClick Обработчик клика на группу.
 */
@Composable
private fun ParticipantGroupsList(
    groups: List<EventParticipantGroup>,
    onGroupClick: (EventParticipantGroup) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Группы участников",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        groups.forEach { group ->
            ParticipantGroupItem(group = group, onClick = { onGroupClick(group) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Айтем группы участников.
 * @param group Данные группы.
 * @param onClick Обработчик клика.
 */
@Composable
private fun ParticipantGroupItem(
    group: EventParticipantGroup,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = group.title,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Участников: ${group.registeredParticipant}/${group.maxParticipant}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Spacer(modifier: Modifier) {
    Spacer(modifier = modifier)
}

@Preview(showBackground = true, name = "Регистрация")
@Composable
private fun EventDetailsRegistrationPreview() {
    MaterialTheme {
        Surface {
            ScrollableColumnScreenWithImageAnimation(
                state = EventDetailsState(
                    eventDetails = mockEvent(EventStatus.REGISTRATION)
                ),
                onAction = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Результаты")
@Composable
private fun EventDetailsResultsPreview() {
    MaterialTheme {
        Surface {
            ScrollableColumnScreenWithImageAnimation(
                state = EventDetailsState(
                    eventDetails = mockEvent(EventStatus.FINISHED)
                ),
                onAction = {}
            )
        }
    }
}

/**
 * Вспомогательная функция для создания мока события.
 */
private fun mockEvent(status: EventStatus) = CyclicEventDetails(
    eventId = 1L,
    organizationId = "org_1",
    title = "Марафон \"Путь к успеху\"",
    description = "Большой забег через весь город.",
    startDate = System.currentTimeMillis(),
    endDate = System.currentTimeMillis() + 86400000L,
    endRegistrationDate = System.currentTimeMillis() - 3600000L,
    maxParticipants = 500,
    city = "Москва",
    status = status,
    participantGroups = listOf(
        EventParticipantGroup(1, "М21", "Профессионалы", 100, 45)
    ),
    eventType = EventType.CyclicEvent.Orienteering
)
