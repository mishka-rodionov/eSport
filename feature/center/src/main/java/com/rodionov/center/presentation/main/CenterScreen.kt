package com.rodionov.center.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.clickRipple
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.CenterEffects
import com.rodionov.center.data.main.CenterState
import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.orienteering.PunchingSystem
import com.rodionov.resources.R
import com.rodionov.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

/**
 * Composable-функция, представляющая главный экран раздела "Центр".
 *
 * Этот экран отображает различное содержимое в зависимости от статуса аутентификации пользователя.
 * Если пользователь аутентифицирован (`state.isAuthed` равно true), он показывает кнопку "Создать новое событие"
 * и список событий, управляемых пользователем (`ControlledEvents`).
 * Если пользователь не аутентифицирован, отображается сообщение с предложением войти или зарегистрироваться.
 *
 * Функция наблюдает за состоянием [CenterState] из [CenterViewModel] для соответствующего обновления UI.
 * `LaunchedEffect` используется для запуска процесса инициализации в ViewModel при изменении состояния.
 *
 * @param viewModel Экземпляр [CenterViewModel], отвечающий за бизнес-логику и управление состоянием этого экрана. Предоставляется через Koin `koinViewModel()`.
 */
@Composable
fun CenterScreen(viewModel: CenterViewModel = koinViewModel()) {

    val state by viewModel.state.collectAsState()
    val handleEffects = remember { viewModel::handleEffects }

    LaunchedEffect(state) {
        viewModel.initialize()
    }

    CenterScreenContent(state, handleEffects)

}

@Composable
fun ControlledEvents(state: CenterState, userAction: (CenterEffects) -> Unit) {
    LazyColumn(modifier = Modifier.padding(top = Dimens.SIZE_HALF.dp)) {
        itemsIndexed(state.controlledEvents) { index, item ->
            EventContent(item.competition, item.competitionId, userAction)
            if (index < state.controlledEvents.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun EventContent(competition: Competition, eventId: Long,  userAction: (CenterEffects) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickRipple {
                userAction.invoke(CenterEffects.OpenOrienteeringEventControl(eventId))
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.map_24dp),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .padding(horizontal = 8.dp)
        ) {
            Text(text = competition.title)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(text = "Город: ${competition.address}")
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Дата: ${DateTimeFormat.formatDate(competition.date)}")
            }
        }
        IconButton(onClick = {
            userAction.invoke(CenterEffects.OpenOrienteeringEditor(eventId))
        }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.edit),
                contentDescription = "Edit event",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

}

@Preview
@Composable
private fun CenterScreenAuthPreview() {
    CenterScreenContent(
        state = CenterState(
            isAuthed = true,
            controlledEvents = listOf(
                OrienteeringCompetition(
                    competitionId = 1L,
                    competition = Competition(
                        title = "Чемпионат города по спортивному ориентированию",
                        date = LocalDate.now(),
                        address = "Москва",
                        kindOfSport = KindOfSport.Orienteering,
                        description = "",
                        mainOrganizer = "123",
                        coordinates = Coordinates(latitude = 0.0, longitude = 0.0),
                    ),
                    direction = OrienteeringDirection.FORWARD,
                    punchingSystem = PunchingSystem.PUNCH
                ),

                OrienteeringCompetition(
                    competitionId = 1L,
                    competition = Competition(
                        title = "Весенний спринт",
                        date = LocalDate.now().plusDays(10),
                        address = "Санкт-Петербург",
                        kindOfSport = KindOfSport.Orienteering,
                        description = "",
                        mainOrganizer = "123",
                        coordinates = Coordinates(latitude = 0.0, longitude = 0.0),
                    ),
                    direction = OrienteeringDirection.FORWARD,
                    punchingSystem = PunchingSystem.PUNCH
                )
            )
        ),
        handleEffects = {}
    )
}

@Preview
@Composable
private fun CenterScreenNotAuthPreview() {
    CenterScreenContent(
        state = CenterState(isAuthed = false),
        handleEffects = {}
    )
}

@Composable
private fun CenterScreenContent(state: CenterState, handleEffects: (CenterEffects) -> Unit) {
    // Replaced the original body of CenterScreen to make it previewable
    Column {
        if (state.isAuthed) {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                onClick = {
                    handleEffects(CenterEffects.OpenKindOfSports)
                },
                content = {
                    Text("Создать новое событие")
                }
            )
            ControlledEvents(state, handleEffects)
        } else {
            Text("Чтобы создать новое событие вы должны зарегестрироваться или войти в свой аккаунт.")
        }
    }
}