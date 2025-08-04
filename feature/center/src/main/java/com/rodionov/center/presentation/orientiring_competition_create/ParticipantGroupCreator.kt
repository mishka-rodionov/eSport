package com.rodionov.center.presentation.orientiring_competition_create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DSBottomDialog
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.OrienteeringCreatorAction
import com.rodionov.center.data.OrienteeringCreatorState
import com.rodionov.domain.models.ParticipantGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantGroupEditor(
    userAction: (OrienteeringCreatorAction) -> Unit,
    state: OrienteeringCreatorState,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var groupTitle by remember { mutableStateOf(if (state.editGroupIndex == -1) "" else state.participantGroups[state.editGroupIndex].title) }
    var distance by remember { mutableStateOf(if (state.editGroupIndex == -1) "" else state.participantGroups[state.editGroupIndex].distance.toString()) }
    var countOfControls by remember { mutableIntStateOf(if (state.editGroupIndex == -1) 0 else state.participantGroups[state.editGroupIndex].countOfControls) }
//    var sequenceOfControl by remember { mutableStateOf("") }
    var maxTime by remember { mutableIntStateOf(if (state.editGroupIndex == -1) 0 else state.participantGroups[state.editGroupIndex].maxTimeInMinute) }
    DSBottomDialog(
        sheetState = sheetState,
        sheetContent = {
            Column(modifier = Modifier.padding(horizontal = Dimens.SIZE_HALF.dp)) {
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Название группы")
                    },
                    isError = state.errors.isGroupTitleError,
                    text = groupTitle,
                    onValueChanged = {
                        groupTitle = it
                    })
                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Дистанция, км")
                    },
                    isError = state.errors.isGroupDistanceError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    text = distance,
                    onValueChanged = { dist ->
                        // Заменяем точку на запятую (если хочешь поддерживать оба варианта)
                        val sanitizedInput = dist.replace(',', '.')

                        // Регулярное выражение: цифры, опционально запятая, и до 2 цифр после неё
                        val decimalRegex = Regex("^\\d{0,7}([.]\\d{0,2})?$")

                        if (sanitizedInput.isEmpty() || sanitizedInput.matches(decimalRegex)) {
                            distance = sanitizedInput
                        }
                    })
                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Кол-во КП")
                    },
                    isError = state.errors.isCountOfControlsError,
                    text = countOfControls.takeIf { it != 0 }?.toString() ?: "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    onValueChanged = { coc ->
                        countOfControls = coc.takeIf { it.isNotBlank() }?.trim()?.toInt() ?: 0
                    })
                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
//                DSTextInput(
//                    modifier = Modifier.fillMaxWidth(),
//                    label = {
//                        Text(text = "Порядок КП")
//                    },
//                    text = sequenceOfControl,
//                    onValueChanged = { soc ->
//                        sequenceOfControl = soc
//                    })
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Контрольное время в мин.")
                    },
                    isError = state.errors.isMaxTimeError,
                    text = maxTime.takeIf { it != 0 }?.toString() ?: "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    onValueChanged = { time ->
                        maxTime = time.takeIf { it.isNotBlank() }?.trim()?.toInt() ?: 0
                    })
                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
                with(state.errors) {
                    if (isGroupTitleError || isGroupDistanceError || isCountOfControlsError || isMaxTimeError){
                        Text(text = "Все поля должны быть корректно заполнены", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
                    }
                }
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    userAction.invoke(
                        OrienteeringCreatorAction.CreateParticipantGroup(
                            participantGroup = ParticipantGroup(
                                groupId = -1,
                                competitionId = -1,
                                title = groupTitle,
                                distance = distance.toDoubleOrNull() ?: 0.0,
                                countOfControls = countOfControls,
                                maxTimeInMinute = maxTime
                            ),
                            index = state.editGroupIndex
                        )
                    )
                }) {
                    Text(text = "Сохранить группу")
                }
            }
        },
        onDismiss = {
            userAction.invoke(OrienteeringCreatorAction.ShowGroupCreateDialog)
        },
    )
}