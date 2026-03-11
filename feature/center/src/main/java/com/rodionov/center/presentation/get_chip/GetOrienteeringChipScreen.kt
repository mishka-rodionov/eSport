package com.rodionov.center.presentation.get_chip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DSTextInput
import com.rodionov.center.data.get_chip.GetOrienteeringChipAction
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import org.koin.androidx.compose.koinViewModel

/**
 * Экран для выдачи чипов участникам соревнований.
 * Содержит вьюпейджер (реализованный через табы) по группам участников.
 *
 * @param competitionId Идентификатор соревнования.
 * @param viewModel Модель представления для экрана выдачи чипов.
 */
@Composable
fun GetOrienteeringChipScreen(
    competitionId: Long,
    viewModel: GetOrienteeringChipViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.loadParticipants(competitionId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (state.groupsWithParticipants.isNotEmpty()) {
            val groups = state.groupsWithParticipants
            // Логика отображения табов: если групп больше 4, используем скроллируемый ряд,
            // если 4 и меньше — обычный ряд, который растягивает табы по всей ширине.
            if (groups.size > 4) {
                SecondaryScrollableTabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = selectedTabIndex.coerceIn(0, groups.size - 1),
                    edgePadding = 0.dp, // Убираем отступ, чтобы табы занимали всю доступную ширину
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {} // Убираем стандартный разделитель
                ) {
                    groups.forEachIndexed { index, groupWithParticipants ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(text = groupWithParticipants.group.title) }
                        )
                    }
                }
            } else {
                SecondaryTabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = selectedTabIndex.coerceIn(0, groups.size - 1),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {} // Убираем стандартный разделитель
                ) {
                    groups.forEachIndexed { index, groupWithParticipants ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(text = groupWithParticipants.group.title) }
                        )
                    }
                }
            }

            val currentGroup = state.groupsWithParticipants.getOrNull(selectedTabIndex)

            if (currentGroup != null) {
                Column(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(currentGroup.participants, key = { it.id }) { participant ->
                            ParticipantChipItem(
                                participant = participant,
                                onChipGivenChanged = { isGiven ->
                                    viewModel.onAction(
                                        GetOrienteeringChipAction.ToggleChipGiven(
                                            participant.id,
                                            isGiven
                                        )
                                    )
                                },
                                onChipNumberChanged = { newNumber ->
                                    viewModel.onAction(
                                        GetOrienteeringChipAction.UpdateChipNumber(
                                            participant.id,
                                            newNumber
                                        )
                                    )
                                }
                            )
                            HorizontalDivider()
                        }
                    }

                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        onClick = { viewModel.onAction(GetOrienteeringChipAction.SaveChanges) },
                        enabled = !state.isSaving
                    ) {
                        Text(text = if (state.isSaving) "Сохранение..." else "Сохранить изменения")
                    }
                }
            }
        } else if (state.isLoading) {
            Text(
                text = "Загрузка участников...",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = "Участники не найдены",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Элемент списка участника для выдачи чипа.
 *
 * @param participant Данные участника.
 * @param onChipNumberChanged Обработчик изменения номера чипа.
 * @param onChipGivenChanged Обработчик изменения состояния выдачи чипа.
 */
@Composable
private fun ParticipantChipItem(
    participant: OrienteeringParticipant,
    onChipNumberChanged: (String) -> Unit,
    onChipGivenChanged: (Boolean) -> Unit
) {
    // Состояние чекбокса теперь берется напрямую из модели участника (isChipGiven),
    // что предотвращает его сброс при переключении табов.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${participant.lastName} ${participant.firstName}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Старт: ${participant.startTime}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        DSTextInput(
            modifier = Modifier.width(100.dp),
            text = participant.chipNumber,
            onValueChanged = onChipNumberChanged,
            label = { Text("Чип") }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Checkbox(
            checked = participant.isChipGiven,
            onCheckedChange = onChipGivenChanged
        )
    }
}
