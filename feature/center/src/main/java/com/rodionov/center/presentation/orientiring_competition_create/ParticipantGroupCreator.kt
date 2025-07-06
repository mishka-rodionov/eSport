package com.rodionov.center.presentation.orientiring_competition_create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.rodionov.center.data.OrienteeringCreatorEffects
import com.rodionov.domain.models.ParticipantGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantGroupEditor(onExit: () -> Unit, userAction: (OrienteeringCreatorEffects) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var groupTitle by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var countOfControls by remember { mutableIntStateOf(0) }
//    var sequenceOfControl by remember { mutableStateOf("") }
    var maxTime by remember { mutableIntStateOf(0) }
    DSBottomDialog(
        sheetState = sheetState,
        sheetContent = {
            Column(modifier = Modifier.padding(horizontal = Dimens.SIZE_HALF.dp)) {
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Название группы")
                    },
                    text = groupTitle,
                    onValueChanged = {
                        groupTitle = it
                    })
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Дистанция")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal ),
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
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Кол-во КП")
                    },
                    text = countOfControls.takeIf { it != 0 }?.toString() ?: "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    onValueChanged = { coc ->
                        countOfControls = coc.takeIf { it.isNotBlank() }?.trim()?.toInt() ?: 0
                    })
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
                    text = maxTime.takeIf { it != 0 }?.toString() ?: "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    onValueChanged = { time ->
                        maxTime = time.takeIf { it.isNotBlank() }?.trim()?.toInt() ?: 0
                    })
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    userAction.invoke(OrienteeringCreatorEffects.CreateParticipantGroup(
                        participantGroup = ParticipantGroup(
                            title = groupTitle,
                            distance = distance.toDoubleOrNull() ?: return@Button,
                            countOfControls = countOfControls,
//                            sequenceOfControl = sequenceOfControl.split(",").map { it.toInt() },
                            maxTimeInMinute = maxTime
                        )
                    ))
                    onExit.invoke()
                }) {
                    Text(text = "Сохранить группу")
                }
            }
        },
        onDismiss = {
            onExit.invoke()
        },
    )
}