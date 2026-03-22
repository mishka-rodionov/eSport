package com.rodionov.center.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.components.clickRipple
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.CenterEffects
import com.rodionov.center.data.main.CenterState
import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.orienteering.CompetitionStatus
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.orienteering.PunchingSystem
import com.rodionov.domain.models.orienteering.ResultsStatus
import com.rodionov.domain.models.orienteering.StartTimeMode
import com.rodionov.resources.R
import com.rodionov.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel

/**
 * Главный экран раздела "Центр" для управления событиями пользователя.
 */
@Composable
fun CenterScreen(viewModel: CenterViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val handleEffects = remember { viewModel::handleEffects }

    LaunchedEffect(null) {
        viewModel.initialize()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        CenterScreenContent(state, handleEffects)
    }
}

/**
 * Контент экрана в зависимости от авторизации.
 */
@Composable
private fun CenterScreenContent(state: CenterState, handleEffects: (CenterEffects) -> Unit) {
    if (state.isAuthed) {
        AuthorizedCenterContent(state, handleEffects)
    } else {
        UnauthorizedCenterView()
    }
}

/**
 * Вид экрана для авторизованного пользователя.
 */
@Composable
private fun AuthorizedCenterContent(state: CenterState, handleEffects: (CenterEffects) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.SIZE_BASE.dp)
    ) {
        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

        Text(
            text = "Управление событиями",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

        // Кнопка создания нового события
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            onClick = { handleEffects(CenterEffects.OpenKindOfSports) },
            shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_add_24px),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Создать новое событие", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

        if (state.controlledEvents.isEmpty()) {
            EmptyControlledEventsView()
        } else {
            Text(
                text = "Мои старты (${state.controlledEvents.size})",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ControlledEventsList(state, handleEffects)
        }
    }
}

/**
 * Список событий, которыми управляет пользователь.
 */
@Composable
private fun ControlledEventsList(state: CenterState, userAction: (CenterEffects) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp),
        contentPadding = PaddingValues(bottom = Dimens.SIZE_BASE.dp)
    ) {
        itemsIndexed(state.controlledEvents) { _, item ->
            EventControlCard(item.competition, item.localCompetitionId, userAction)
        }
    }
}

/**
 * Карточка управляемого события.
 */
@Composable
private fun EventControlCard(
    competition: Competition,
    eventId: Long,
    userAction: (CenterEffects) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickRipple {
                userAction.invoke(CenterEffects.OpenOrienteeringEventControl(eventId))
            },
        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(Dimens.SIZE_BASE.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Миниатюра события
            Image(
                painter = painterResource(R.drawable.forest),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(Dimens.SIZE_HALF.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(Dimens.SIZE_BASE.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = competition.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_location_on_24px),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = competition.address ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_date_range_24px), // Исправлено на правильный ресурс если доступен, иначе ic_date_range
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = DateTimeFormat.transformLongToDisplayDate(competition.startDate),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Кнопка редактирования настроек события
            IconButton(
                onClick = { userAction.invoke(CenterEffects.OpenOrienteeringEditor(eventId)) },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.edit),
                    contentDescription = "Edit event",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Экран для неавторизованного пользователя.
 */
@Composable
private fun UnauthorizedCenterView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.SIZE_DOUBLE.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_build_24px),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
        Text(
            text = "Станьте организатором",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Войдите в аккаунт, чтобы создавать свои соревнования и управлять участниками.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Заглушка, если у организатора еще нет событий.
 */
@Composable
private fun EmptyControlledEventsView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "У вас пока нет созданных событий.\nНажмите кнопку выше, чтобы начать.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// Поправка для ресурса иконки даты, если в предыдущих шагах использовался ic_date_range_24px
private val R.datasource_ic_date_range_24px: Int get() = R.drawable.ic_date_range_24px

@Preview(showBackground = true)
@Composable
private fun CenterScreenAuthPreview() {
    CenterScreenContent(
        state = CenterState(
            isAuthed = true,
            controlledEvents = listOf(
                OrienteeringCompetition(
                    localCompetitionId = 1L,
                    competition = Competition(
                        remoteId = null,
                        title = "Чемпионат города по ориентированию",
                        startDate = System.currentTimeMillis(),
                        endDate = System.currentTimeMillis() + 86400000L,
                        address = "Москва, Парк Сокольники",
                        kindOfSport = KindOfSport.Orienteering,
                        description = "",
                        mainOrganizerId = 123L,
                        coordinates = Coordinates(latitude = 0.0, longitude = 0.0),
                        status = CompetitionStatus.DRAFT,
                        resultsStatus = ResultsStatus.NOT_PUBLISHED
                    ),
                    direction = OrienteeringDirection.FORWARD,
                    punchingSystem = PunchingSystem.PUNCH,
                    startTimeMode = StartTimeMode.STRICT
                )
            )
        ),
        handleEffects = {}
    )
}
