package com.competra.center.presentation.orientiring_competition_create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
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
import com.competra.designsystem.components.DSBottomDialog
import com.competra.designsystem.components.DSTextInput
import com.competra.designsystem.theme.Dimens
import com.competra.center.data.creator.OrienteeringCreatorAction
import com.competra.center.data.creator.OrienteeringCreatorState
import com.competra.domain.models.orienteering.ControlPoint
import com.competra.domain.models.orienteering.Distance
import com.competra.resources.R

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
    var controlPointsList by remember {
        mutableStateOf<List<Int>>(initialDistance?.controlPoints?.map { it.number }.orEmpty())
    }
    var currentInput by remember { mutableStateOf("") }
    var description by remember { mutableStateOf(initialDistance?.description ?: "") }
    
    // Создаем реквизиторы фокуса для каждого поля
    val titleFocus = remember { FocusRequester() }
    val lengthFocus = remember { FocusRequester() }
    val climbFocus = remember { FocusRequester() }
    val controlsFocus = remember { FocusRequester() }
    val pointsFocus = remember { FocusRequester() }
    val descFocus = remember { FocusRequester() }
    
    LaunchedEffect(controlPointsList) {
        if (controlPointsList.isNotEmpty()) {
            controlsCount = controlPointsList.size.toString()
        }
    }

    val addCurrentInput: () -> Unit = {
        val number = currentInput.trim().toIntOrNull()
        if (number != null) {
            controlPointsList = controlPointsList + number
            currentInput = ""
        }
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current

    DSBottomDialog(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
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
                    supportingText = { Text("Под этим именем участники увидят дистанцию при выборе") },
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
                        supportingText = { Text("Длина по оптимальному маршруту") },
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
                        supportingText = { Text("Суммарный набор высоты") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { pointsFocus.requestFocus() }
                        ),
                        text = climbMeters,
                        onValueChanged = { climbMeters = it }
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                // Количество КП
//                DSTextInput(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .focusRequester(controlsFocus),
//                    label = { Text("Количество КП") },
//                    supportingText = { Text("Сколько КП нужно взять для зачёта прохождения") },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Number,
//                        imeAction = ImeAction.Next
//                    ),
//                    keyboardActions = KeyboardActions(
//                        onNext = { pointsFocus.requestFocus() }
//                    ),
//                    text = controlsCount,
//                    onValueChanged = { controlsCount = it }
//                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                // Список КП (чипы)
                Text(
                    text = "Список КП",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = Dimens.SIZE_QUARTER.dp, bottom = Dimens.SIZE_QUARTER.dp)
                )
                FieldDescription("Номера КП в порядке прохождения по дистанции")

                if (controlPointsList.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SIZE_QUARTER.dp),
                        verticalArrangement = Arrangement.spacedBy(Dimens.SIZE_QUARTER.dp)
                    ) {
                        controlPointsList.forEachIndexed { index, cpNumber ->
                            InputChip(
                                selected = false,
                                onClick = { /* no-op */ },
                                label = { Text(text = cpNumber.toString()) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.close_24px),
                                        contentDescription = "Удалить КП $cpNumber",
                                        modifier = Modifier
                                            .size(Dimens.SIZE_BASE.dp)
                                            .clickable {
                                                controlPointsList = controlPointsList.toMutableList()
                                                    .also { it.removeAt(index) }
                                            }
                                    )
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimens.SIZE_QUARTER.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SIZE_HALF.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DSTextInput(
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(pointsFocus),
                        label = { Text("Добавить КП") },
                        placeholder = { Text("31") },
                        supportingText = { Text("Введите номер КП и нажмите Enter или ✚") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { addCurrentInput() }
                        ),
                        text = currentInput,
                        onValueChanged = { newValue ->
                            currentInput = newValue.filter { it.isDigit() }
                        }
                    )
                    IconButton(onClick = addCurrentInput) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_add_24px),
                            contentDescription = "Добавить КП"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                // Описание
                DSTextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(descFocus),
                    label = { Text("Описание") },
                    supportingText = { Text("Дополнительные сведения о дистанции для участников") },
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

                        val points = controlPointsList.map { ControlPoint(number = it) }

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
