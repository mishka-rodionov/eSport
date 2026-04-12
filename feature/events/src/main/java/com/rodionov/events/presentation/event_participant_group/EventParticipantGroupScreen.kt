package com.rodionov.events.presentation.event_participant_group

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodionov.domain.models.Participant
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.events.data.event_participant_group.EventParticipantGroupState
import com.rodionov.ui.BaseAction
import org.koin.androidx.compose.koinViewModel

/**
 * Экран списка участников группы события.
 * @param eventId Идентификатор события.
 * @param participantGroup Данные группы участников.
 * @param viewModel Вьюмодель экрана.
 */
@Composable
fun EventParticipantGroupScreen(
    eventId: Long,
    participantGroup: EventParticipantGroup,
    viewModel: EventParticipantGroupViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(eventId, participantGroup) {
        viewModel.initialize(eventId, participantGroup)
    }

    EventParticipantGroupContent(
        participantGroup = participantGroup,
        state = state,
        onAction = viewModel::onAction
    )
}

/**
 * Контент экрана группы участников события.
 * @param participantGroup Данные группы.
 * @param state Состояние экрана.
 * @param onAction Обработчик действий пользователя.
 */
@Composable
private fun EventParticipantGroupContent(
    participantGroup: EventParticipantGroup,
    state: EventParticipantGroupState,
    onAction: (BaseAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок группы и кнопка регистрации
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = participantGroup.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            
            RegistrationButton(
                isUserRegistered = state.isUserRegistered,
                isRegistering = state.isRegistering,
                onAction = onAction
            )
        }

        Text(
            text = "Участники (${state.participants.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        HorizontalDivider()

        // Список участников
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.participants) { participant ->
                ParticipantItem(participant = participant)
            }
        }
    }
}

/**
 * Кнопка регистрации/отмены регистрации.
 * @param isUserRegistered Зарегистрирован ли пользователь.
 * @param isRegistering Состояние процесса регистрации.
 * @param onAction Обработчик действий.
 */
@Composable
private fun RegistrationButton(
    isUserRegistered: Boolean,
    isRegistering: Boolean,
    onAction: (BaseAction) -> Unit
) {
    if (isUserRegistered) {
        Button(
            onClick = { onAction(EventParticipantGroupAction.CancelRegistration) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            enabled = !isRegistering
        ) {
            if (isRegistering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            } else {
                Text(text = "Отменить регистрацию")
            }
        }
    } else {
        Button(
            onClick = { onAction(EventParticipantGroupAction.RegisterUser) },
            enabled = !isRegistering
        ) {
            if (isRegistering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = "Зарегистрироваться")
            }
        }
    }
}

/**
 * Айтем участника в списке.
 * @param participant Данные участника.
 */
@Composable
private fun ParticipantItem(participant: Participant) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = (participant as? OrienteeringParticipant)?.let { "${it.firstName} ${it.lastName}" }
                ?: "ID: ${participant.id}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "User: ${participant.userId}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EventParticipantGroupScreenPreview() {
    MaterialTheme {
        Surface {
            EventParticipantGroupContent(
                participantGroup = EventParticipantGroup(
                    groupId = "1",
                    title = "М21",
                    description = "Мужчины 21 год и старше",
                    maxParticipant = 100,
                    registeredParticipant = 3
                ),
                state = EventParticipantGroupState(
                    participants = listOf(
                        OrienteeringParticipant(
                            id = 1, userId = "u1", firstName = "Иван", lastName = "Иванов",
                            groupId = 1, groupName = "М21", competitionId = 1, commandName = "Клуб 1",
                            startNumber = "1", startTime = 0L, chipNumber = "111", comment = "", isChipGiven = true
                        ),
                        OrienteeringParticipant(
                            id = 2, userId = "u2", firstName = "Петр", lastName = "Петров",
                            groupId = 1, groupName = "М21", competitionId = 1, commandName = "Клуб 2",
                            startNumber = "2", startTime = 0L, chipNumber = "222", comment = "", isChipGiven = true
                        )
                    )
                ),
                onAction = {}
            )
        }
    }
}
