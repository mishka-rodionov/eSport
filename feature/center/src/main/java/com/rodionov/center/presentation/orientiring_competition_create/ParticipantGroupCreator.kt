package com.rodionov.center.presentation.orientiring_competition_create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DSBottomDialog
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.center.data.creator.OrienteeringCreatorState
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.Distance
import com.rodionov.resources.R

/**
 * Редактор группы участников соревнований по ориентированию.
 * 
 * Включает выбор дистанции из списка существующих для данного соревнования.
 * 
 * @param userAction Обработчик действий ViewModel.
 * @param state Текущее состояние процесса создания.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantGroupEditor(
    userAction: (OrienteeringCreatorAction) -> Unit,
    state: OrienteeringCreatorState,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    val initialGroup = state.participantGroups.getOrNull(state.editGroupIndex)
    
    var groupTitle by remember { mutableStateOf(initialGroup?.title ?: "") }
    var minAge by remember { mutableStateOf(initialGroup?.minAge?.toString() ?: "") }
    var maxAge by remember { mutableStateOf(initialGroup?.maxAge?.toString() ?: "") }
    var maxParticipants by remember { mutableStateOf(initialGroup?.maxParticipants?.toString() ?: "") }
    var selectedGender by remember { mutableStateOf(initialGroup?.gender) }
    
    // Выбранная дистанция для группы
    var selectedDistanceId by remember { mutableStateOf(initialGroup?.distanceId ?: state.distances.firstOrNull()?.id ?: 0L) }

    // Реквизиторы фокуса
    val titleFocus = remember { FocusRequester() }
    val minAgeFocus = remember { FocusRequester() }
    val maxAgeFocus = remember { FocusRequester() }
    val limitFocus = remember { FocusRequester() }

    DSBottomDialog(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SIZE_BASE.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = if (state.editGroupIndex == -1) "Создание группы" else "Редактирование группы",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Название группы
                DSTextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(titleFocus),
                    label = { Text(text = stringResource(R.string.label_participant_group_title)) },
                    isError = state.errors.isGroupTitleError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { minAgeFocus.requestFocus() }),
                    text = groupTitle,
                    onValueChanged = { groupTitle = it }
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

                // Выбор дистанции
                Text(
                    text = "Выберите дистанцию",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                if (state.distances.isEmpty()) {
                    Text(
                        text = "Дистанции не найдены. Создайте их на предыдущем шаге.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.distances) { distance ->
                            DistanceSelectCard(
                                distance = distance,
                                isSelected = selectedDistanceId == distance.id,
                                onSelect = { selectedDistanceId = distance.id }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

                // Возрастные ограничения
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DSTextInput(
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(minAgeFocus),
                        label = { Text(text = "Мин. возраст") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { maxAgeFocus.requestFocus() }),
                        text = minAge,
                        onValueChanged = { minAge = it })

                    DSTextInput(
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(maxAgeFocus),
                        label = { Text(text = "Макс. возраст") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { limitFocus.requestFocus() }),
                        text = maxAge,
                        onValueChanged = { maxAge = it })
                }

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                // Лимит участников
                DSTextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(limitFocus),
                    label = { Text(text = "Лимит участников") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    text = maxParticipants,
                    onValueChanged = { maxParticipants = it }
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

                // Кнопка сохранения
                Button(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
                    enabled = groupTitle.isNotBlank() && selectedDistanceId != 0L,
                    onClick = {
                        userAction.invoke(
                            OrienteeringCreatorAction.CreateParticipantGroup(
                                participantGroup = ParticipantGroup(
                                    groupId = initialGroup?.groupId ?: 0L,
                                    competitionId = state.competitionId ?: 0L,
                                    title = groupTitle,
                                    gender = selectedGender,
                                    minAge = minAge.toIntOrNull(),
                                    maxAge = maxAge.toIntOrNull(),
                                    distanceId = selectedDistanceId, // Сохраняем ID выбранной дистанции
                                    maxParticipants = maxParticipants.toIntOrNull(),
                                    isSynced = false,
                                    lastModified = System.currentTimeMillis()
                                ),
                                index = state.editGroupIndex
                            )
                        )
                    }
                ) {
                    Text(text = stringResource(R.string.label_save_group), fontWeight = FontWeight.Bold)
                }
            }
        },
        onDismiss = {
            userAction.invoke(OrienteeringCreatorAction.HideGroupCreateDialog)
        },
    )
}

/**
 * Карточка выбора дистанции для группы.
 */
@Composable
private fun DistanceSelectCard(
    distance: Distance,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .selectable(
                selected = isSelected,
                onClick = onSelect
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = distance.name ?: "Без названия",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                RadioButton(
                    selected = isSelected,
                    onClick = onSelect,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Длина: ${distance.lengthMeters}м",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "КП: ${distance.controlsCount}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
