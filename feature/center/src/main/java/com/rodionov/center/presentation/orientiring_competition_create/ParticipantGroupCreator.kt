package com.rodionov.center.presentation.orientiring_competition_create

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.designsystem.components.DSBottomDialog
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.center.data.creator.OrienteeringCreatorState
import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.resources.R

/**
 * Редактор группы участников соревнований по ориентированию.
 * 
 * Обновлен для поддержки новой модели ParticipantGroup.
 * Временно закомментированы поля старой модели (КП, дистанция напрямую), 
 * так как теперь группа ссылается на дистанцию через ID.
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

    DSBottomDialog(
        sheetState = sheetState,
        sheetContent = {
            Column(modifier = Modifier.padding(Dimens.SIZE_BASE.dp)) {
                Text(
                    text = if (state.editGroupIndex == -1) "Создание группы" else "Редактирование группы",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.label_participant_group_title)) },
                    isError = state.errors.isGroupTitleError,
                    text = groupTitle,
                    onValueChanged = { groupTitle = it })

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DSTextInput(
                        modifier = Modifier.weight(1f),
                        label = { Text(text = "Мин. возраст") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        text = minAge,
                        onValueChanged = { minAge = it })

                    DSTextInput(
                        modifier = Modifier.weight(1f),
                        label = { Text(text = "Макс. возраст") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        text = maxAge,
                        onValueChanged = { maxAge = it })
                }

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Лимит участников") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    text = maxParticipants,
                    onValueChanged = { maxParticipants = it })

                Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

                Button(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
                    onClick = {
                        userAction.invoke(
                            OrienteeringCreatorAction.CreateParticipantGroup(
                                participantGroup = ParticipantGroup(
                                    groupId = initialGroup?.groupId ?: 0L,
                                    competitionId = initialGroup?.competitionId ?: 0L,
                                    title = groupTitle,
                                    gender = selectedGender,
                                    minAge = minAge.toIntOrNull(),
                                    maxAge = maxAge.toIntOrNull(),
                                    distanceId = initialGroup?.distanceId ?: 1L, // Mock ID
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
