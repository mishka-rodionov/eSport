package com.rodionov.center.presentation.orientiring_competition_create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.data.navigation.CenterNavigation
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * Пятый экран создания соревнования: Настройка групп участников.
 * 
 * На этом экране пользователь может добавлять, редактировать и просматривать группы участников
 * для текущего соревнования.
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

    Scaffold(
        bottomBar = {
            NavigationButtons(
                onBack = { 
                    viewModel.viewModelScope.launch {
                        viewModel.navigation.navigate(CenterNavigation.CreateDistanceRoute(competitionId))
                    }
                },
                onNext = { viewModel.finishCreation() },
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
                            // Возможность редактирования существующей группы
                            viewModel.onAction(OrienteeringCreatorAction.ShowGroupCreateDialog)
                            // TODO: установить индекс редактируемой группы во вьюмодели
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
                            // Дополнительная информация о группе
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            Button(
                onClick = { viewModel.onAction(OrienteeringCreatorAction.ShowGroupCreateDialog) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добавить группу")
            }
        }
    }

    // Отображение диалога создания/редактирования группы
    if (state.isShowGroupCreateDialog) {
        ParticipantGroupEditor(
            userAction = viewModel::onAction,
            state = state
        )
    }
}
