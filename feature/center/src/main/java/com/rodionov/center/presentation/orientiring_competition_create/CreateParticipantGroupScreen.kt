package com.rodionov.center.presentation.orientiring_competition_create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.center.data.creator.OrienteeringCreatorState
import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.ParticipantGroup
import org.koin.androidx.compose.koinViewModel

/**
 * Пятый экран создания соревнования: Настройка групп участников.
 * 
 * @param competitionId Идентификатор соревнования.
 * @param viewModel Вьюмодель процесса создания.
 */
@Composable
fun CreateParticipantGroupScreen(
    competitionId: Long,
    viewModel: OrienteeringCreatorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize(competitionId)
    }

    CreateParticipantGroupContent(
        state = state,
        onBack = viewModel::back,
        onNext = viewModel::finishCreation,
        onAction = viewModel::onAction
    )

    // Отображение диалога создания/редактирования группы
    if (state.isShowGroupCreateDialog) {
        ParticipantGroupEditor(
            userAction = viewModel::onAction,
            state = state
        )
    }
}

/**
 * Контент экрана настройки групп участников.
 * Выделен отдельно для поддержки Preview.
 */
@Composable
private fun CreateParticipantGroupContent(
    state: OrienteeringCreatorState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onAction: (OrienteeringCreatorAction) -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationButtons(
                onBack = onBack,
                onNext = onNext,
                nextText = "Завершить",
                nextEnabled = state.participantGroups.isNotEmpty()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.SIZE_BASE.dp)
        ) {
            Text(
                text = "Шаг 5: Группы участников",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Список созданных групп
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp)
            ) {
                itemsIndexed(state.participantGroups) { index, group ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        onClick = { 
                            onAction(OrienteeringCreatorAction.ShowGroupCreateDialog)
                        }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = group.title, fontWeight = FontWeight.Bold)
                            if (group.minAge != null || group.maxAge != null) {
                                Text(
                                    text = "Возраст: ${group.minAge ?: 0} - ${group.maxAge ?: "∞"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            Button(
                onClick = { onAction(OrienteeringCreatorAction.ShowGroupCreateDialog) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добавить группу")
            }
        }
    }
}

@Preview(showBackground = true, name = "Empty list")
@Composable
private fun CreateParticipantGroupEmptyPreview() {
    MaterialTheme {
        CreateParticipantGroupContent(
            state = OrienteeringCreatorState(),
            onBack = {},
            onNext = {},
            onAction = {}
        )
    }
}

@Preview(showBackground = true, name = "With groups")
@Composable
private fun CreateParticipantGroupFilledPreview() {
    MaterialTheme {
        CreateParticipantGroupContent(
            state = OrienteeringCreatorState(
                participantGroups = listOf(
                    ParticipantGroup(
                        groupId = 1, competitionId = 1, title = "М21", distanceId = 1, minAge = 21,
                        gender = Gender.MALE,
                    ),
                    ParticipantGroup(groupId = 2, competitionId = 1, title = "Ж18", distanceId = 2, minAge = 16, maxAge = 18, gender = Gender.FEMALE)
                )
            ),
            onBack = {},
            onNext = {},
            onAction = {}
        )
    }
}
