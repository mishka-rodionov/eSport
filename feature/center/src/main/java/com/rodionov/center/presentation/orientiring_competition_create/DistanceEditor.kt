package com.rodionov.center.presentation.orientiring_competition_create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
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
 * Реализован принудительный переход фокуса между полями ввода с использованием FocusRequester
 * и гарантированное скрытие клавиатуры через SoftwareKeyboardController.
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
    
    // Создаем реквизиторы фокуса для каждого поля
    val titleFocus = remember { FocusRequester() }
    val lengthFocus = remember { FocusRequester() }
    val climbFocus = remember { FocusRequester() }
    val controlsFocus = remember { FocusRequester() }
    val pointsFocus = remember { FocusRequester() }
    val descFocus = remember { FocusRequester() }
    
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current

    DSBottomDialog(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SIZE_BASE.dp)
                    .padding(bottom = Dimens.SIZE_BASE.dp)
            ) {
                Text(
                    text = if (state.editDistanceIndex == -1) "Новая дистанция" else "Редактирование дистанции",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = Dimens.SIZE_BASE.dp)
                )

                // Название дистанции
                DSTextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(titleFocus),
                    label = { Text("Название дистанции") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { lengthFocus.requestFocus() }
                    ),
                    text = title,
                    onValueChanged = { title = it }
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Длина
                    DSTextInput(
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(lengthFocus),
                        label = { Text("Длина (м)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { climbFocus.requestFocus() }
                        ),
                        text = lengthMeters,
                        onValueChanged = { lengthMeters = it }
                    )

                    // Набор высоты
                    DSTextInput(
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(climbFocus),
                        label = { Text("Набор высоты (м)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { controlsFocus.requestFocus() }
                        ),
                        text = climbMeters,
                        onValueChanged = { climbMeters = it }
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                // Количество КП
                DSTextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(controlsFocus),
                    label = { Text("Количество КП") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { pointsFocus.requestFocus() }
                    ),
                    text = controlsCount,
                    onValueChanged = { controlsCount = it }
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                // Список КП (через запятую)
                DSTextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(pointsFocus),
                    label = { Text("Список КП (через запятую)") },
                    placeholder = { Text("31, 32, 45, 100") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { descFocus.requestFocus() }
                    ),
                    text = controlPointsStr,
                    onValueChanged = { newValue ->
                        // Фильтруем ввод: разрешаем только цифры, запятые и пробелы
                        val filtered = newValue.filter { it.isDigit() || it == ',' || it == ' ' }
                        controlPointsStr = filtered
                    }
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                // Описание
                DSTextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(descFocus),
                    label = { Text("Описание") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.clearFocus()
                        },
                        onDone = {
                            descFocus.freeFocus()
                            focusManager.clearFocus(force = true)
                            view.clearFocus()
                            keyboardController?.hide()
                        }
                    ),
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
                        // Сбрасываем фокус и скрываем клавиатуру при нажатии кнопки сохранения
                        focusManager.clearFocus()
                        keyboardController?.hide()

                        val points = controlPointsStr.split(",")
                            .mapNotNull { it.trim().toIntOrNull() }
                            .map { ControlPoint(number = it) }

                        userAction.invoke(
                            OrienteeringCreatorAction.CreateDistance(
                                distance = Distance(
                                    id = if (state.editDistanceIndex == -1) 0L else state.distances[state.editDistanceIndex].id,
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
