package com.rodionov.center.presentation.orientiring_competition_create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DSBottomDialog
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.center.data.creator.OrienteeringCreatorState
import com.rodionov.domain.models.orienteering.ControlPoint
import com.rodionov.domain.models.orienteering.Distance
import com.rodionov.resources.R

/**
 * Диалог редактирования/создания дистанции.
 * 
 * @param userAction Обработчик действий ViewModel.
 * @param state Текущее состояние процесса создания.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistanceEditor(
    userAction: (OrienteeringCreatorAction) -> Unit,
    state: OrienteeringCreatorState,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val initialDistance = state.distances.getOrNull(state.editDistanceIndex)

    var title by remember { mutableStateOf(initialDistance?.name ?: "") }
    var lengthMeters by remember { mutableStateOf(initialDistance?.lengthMeters?.toString() ?: "") }
    var climbMeters by remember { mutableStateOf(initialDistance?.climbMeters?.toString() ?: "") }
    var controlsCount by remember {
        mutableStateOf(
            initialDistance?.controlsCount?.toString() ?: ""
        )
    }
    var controlPointsStr by remember {
        mutableStateOf(initialDistance?.controlPoints?.joinToString(", ") { it.number.toString() }
            ?: "")
    }
    var description by remember { mutableStateOf(initialDistance?.description ?: "") }
    val focusManager = LocalFocusManager.current

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
                    text = if (state.editDistanceIndex == -1) "Новая дистанция" else "Редактирование дистанции",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Название дистанции") },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    ),
                    text = title,
                    onValueChanged = { title = it }
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DSTextInput(
                        modifier = Modifier.weight(1f),
                        label = { Text("Длина (м)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Next) }
                        ),
                        text = lengthMeters,
                        onValueChanged = { lengthMeters = it }
                    )

                    DSTextInput(
                        modifier = Modifier.weight(1f),
                        label = { Text("Набор высоты (м)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Next) }
                        ),
                        text = climbMeters,
                        onValueChanged = { climbMeters = it }
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Количество КП") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    ),
                    text = controlsCount,
                    onValueChanged = { controlsCount = it }
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Список КП (через запятую)") },
                    placeholder = { Text("31, 32, 45, 100") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    ),
                    text = controlPointsStr,
                    onValueChanged = { controlPointsStr = it }
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Описание") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    text = description,
                    onValueChanged = { description = it }
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
                    onClick = {
                        val points = controlPointsStr.split(",")
                            .mapNotNull { it.trim().toIntOrNull() }
                            .map { ControlPoint(number = it) }

                        userAction.invoke(
                            OrienteeringCreatorAction.CreateDistance(
                                distance = Distance(
                                    competitionId = state.competitionId ?: 0L,
                                    name = title,
                                    lengthMeters = lengthMeters.toIntOrNull() ?: 0,
                                    climbMeters = climbMeters.toIntOrNull() ?: 0,
                                    controlsCount = controlsCount.toIntOrNull() ?: 0,
                                    description = description,
                                    controlPoints = points
                                ),
                                index = state.editDistanceIndex
                            )
                        )
                    }
                ) {
                    Text(text = "Готово", fontWeight = FontWeight.Bold)
                }
            }
        },
        onDismiss = {
            userAction.invoke(OrienteeringCreatorAction.HideDistanceCreateDialog)
        },
    )
}
