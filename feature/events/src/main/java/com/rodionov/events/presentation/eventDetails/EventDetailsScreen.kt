package com.rodionov.events.presentation.eventDetails

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodionov.domain.models.cyclic_event.CyclicEventDetails
import com.rodionov.events.R
import com.rodionov.events.data.details.EventDetailsState
import com.rodionov.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel

@Composable
fun EventDetailsScreen(viewModel: EventDetailsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    ScrollableColumnScreenWithImageAnimation(state)
}

/**
 * Основной контент экрана деталей события с анимацией изображения при прокрутке.
 * @param state Состояние экрана.
 */
@Composable
fun ScrollableColumnScreenWithImageAnimation(
    state: EventDetailsState
) {
    val scrollState = rememberScrollState()
    val imageHeightPx = with(LocalDensity.current) { 250.dp.toPx() }

    // Анимация прозрачности изображения
    // Изображение полностью видимо, когда прокрутка в самом верху (scrollState.value == 0)
    // И полностью невидимо, когда прокрутка достигает высоты изображения
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
            painter = painterResource(id = R.drawable.map_24dp),
            contentDescription = "Header Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .graphicsLayer { // Используем graphicsLayer для применения alpha
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
            Text(text = DateTimeFormat.transformLongToDisplayDate(state.eventDetails?.startDate), modifier = Modifier.weight(1f))
//            Text(text = state.eventDetails?.coordinates?.latitude?.toString() ?: "", modifier = Modifier.weight(1f))
        }

        Button(
            onClick = { /* TODO: Регистрация */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Зарегистрироваться")
        }

        Text(
            text = state.eventDetails?.description ?: "",
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Заполненное состояние")
@Composable
private fun EventDetailsScreenPreview() {
    MaterialTheme {
        Surface {
            ScrollableColumnScreenWithImageAnimation(
                state = EventDetailsState(
                    eventDetails = CyclicEventDetails(
                        eventId = 1L,
                        organizationId = "org_1",
                        title = "Марафон \"Путь к успеху\"",
                        description = "Большой забег через весь город. Приглашаем всех желающих испытать свои силы и насладиться видами нашего прекрасного города.",
                        startDate = System.currentTimeMillis(),
                        endDate = System.currentTimeMillis() + 86400000L,
                        endRegistrationDate = System.currentTimeMillis() - 3600000L,
                        maxParticipants = 500,
                        city = "Москва",
                        participantGroups = emptyList()
                    )
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Пустое состояние")
@Composable
private fun EventDetailsScreenEmptyPreview() {
    MaterialTheme {
        Surface {
            ScrollableColumnScreenWithImageAnimation(
                state = EventDetailsState(eventDetails = null)
            )
        }
    }
}
