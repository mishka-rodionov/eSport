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
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.ControlPoint
import com.rodionov.resources.R
import kotlin.math.roundToInt

/**
 * Редактор группы участников соревнований по ориентированию, представленный в виде модального нижнего окна (bottom sheet).
 *
 * Позволяет настраивать основные параметры группы: название, дистанцию и контрольное время.
 * Реализует интерактивное управление списком контрольных пунктов (КП) с возможностью:
 * - Добавления новых КП через ввод номера.
 * - Удаления существующих КП.
 * - Изменения порядка следования КП с помощью перетаскивания (Drag-and-Drop) по долгому нажатию.
 *
 * @param userAction Функция обратного вызова для обработки действий пользователя (сохранение группы, закрытие диалога).
 * @param state Текущее состояние экрана создания соревнования, содержащее данные о группах, ошибки валидации и индекс редактируемой группы.
 */
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
    var maxTime by remember { mutableIntStateOf(if (state.editGroupIndex == -1) 0 else state.participantGroups[state.editGroupIndex].maxTimeInMinute) }

    var controlPoints by remember {
        mutableStateOf(
            if (state.editGroupIndex == -1) emptyList<ControlPoint>()
            else state.participantGroups[state.editGroupIndex].controlPoints
        )
    }
    var cpInput by remember { mutableStateOf("") }

    // Drag and Drop State
    val lazyListState = rememberLazyListState()
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    DSBottomDialog(
        sheetState = sheetState,
        sheetContent = {
            Column(modifier = Modifier.padding(horizontal = Dimens.SIZE_HALF.dp).padding(bottom = Dimens.SIZE_HALF.dp)) {
                // ... (Блоки ввода Названия и Дистанции остаются без изменений)
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Название группы") },
                    isError = state.errors.isGroupTitleError,
                    text = groupTitle,
                    onValueChanged = { groupTitle = it })

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Дистанция, км") },
                    isError = state.errors.isGroupDistanceError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    text = distance,
                    onValueChanged = { dist ->
                        val sanitizedInput = dist.replace(',', '.')
                        val decimalRegex = Regex("^\\d{0,7}([.]\\d{0,2})?$")
                        if (sanitizedInput.isEmpty() || sanitizedInput.matches(decimalRegex)) {
                            distance = sanitizedInput
                        }
                    })

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Добавить КП (номер)") },
                    text = cpInput,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChanged = { input ->
                        if (input.endsWith(" ") || input.endsWith(",") || input.endsWith(".") || input.endsWith("\n")) {
                            val number = input.dropLast(1).trim().toIntOrNull()
                            if (number != null) {
                                controlPoints = controlPoints + ControlPoint(number = number)
                                countOfControls = controlPoints.size
                                cpInput = ""
                            } else {
                                cpInput = input
                            }
                        } else {
                            cpInput = input
                        }
                    })

                Spacer(modifier = Modifier.height(Dimens.SIZE_QUARTER.dp))

                if (controlPoints.isNotEmpty()) {
                    Text(
                        text = "Дистанция (зажмите для перемещения):",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Константа ширины чипа + отступ (80dp чип + 8dp spacing)
                    // Используем LocalDensity для перевода dp в пиксели для точности расчетов
                    val density = androidx.compose.ui.platform.LocalDensity.current
                    val itemTotalWidthPx = with(density) { (72.dp + 8.dp).toPx() }
                    val currentItemWidth by rememberUpdatedState(itemTotalWidthPx)

                    LazyRow(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Используем identityHashCode для стабильного ключа, чтобы перетаскивание не прерывалось при смене индекса
                        itemsIndexed(controlPoints, key = { _, cp -> System.identityHashCode(cp) }) { index, cp ->
                            val currentIdx by rememberUpdatedState(index)
                            val isDragging = draggingIndex == index
                            val scale by animateFloatAsState(if (isDragging) 1.2f else 1.0f, label = "scale")
                            val zIndex = if (isDragging) 10f else 1f

                            Box(
                                modifier = Modifier
                                    .zIndex(zIndex)
                                    .scale(scale)
                                    .offset {
                                        if (isDragging) IntOffset(dragOffset.roundToInt(), 0)
                                        else IntOffset.Zero
                                    }
                                    .shadow(if (isDragging) 8.dp else 0.dp, RoundedCornerShape(16.dp))
                                    .pointerInput(Unit) { // Unit, чтобы жест не сбрасывался при recomposition
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = {
                                                // Важно: запоминаем актуальный индекс на момент начала
                                                draggingIndex = currentIdx
                                            },
                                            onDragEnd = {
                                                draggingIndex = null
                                                dragOffset = 0f
                                            },
                                            onDragCancel = {
                                                draggingIndex = null
                                                dragOffset = 0f
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                dragOffset += dragAmount.x

                                                val currentIdxForDrag = draggingIndex ?: return@detectDragGesturesAfterLongPress

                                                // Рассчитываем, на сколько позиций переместился палец
                                                val shift = (dragOffset / currentItemWidth).roundToInt()
                                                val targetIndex = (currentIdxForDrag + shift).coerceIn(0, controlPoints.size - 1)

                                                if (targetIndex != currentIdxForDrag) {
                                                    val newList = controlPoints.toMutableList()
                                                    val item = newList.removeAt(currentIdxForDrag)
                                                    newList.add(targetIndex, item)

                                                    // Обновляем состояние
                                                    controlPoints = newList

                                                    // Корректируем оффсет, чтобы элемент визуально оставался под пальцем
                                                    dragOffset -= (targetIndex - currentIdxForDrag) * currentItemWidth
                                                    draggingIndex = targetIndex
                                                }
                                            }
                                        )
                                    }
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isDragging) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    border = CardDefaults.outlinedCardBorder()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = cp.number.toString(),
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.close_24px),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable {
                                                    controlPoints = controlPoints.toMutableList().apply { removeAt(index) }
                                                    countOfControls = controlPoints.size
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

                // ... (Остальная часть формы: Контрольное время и кнопка Сохранить)
                DSTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Контрольное время в мин.") },
                    isError = state.errors.isMaxTimeError,
                    text = maxTime.takeIf { it != 0 }?.toString() ?: "",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChanged = { time ->
                        maxTime = time.takeIf { it.isNotBlank() }?.trim()?.toIntOrNull() ?: 0
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
                                groupId = if (state.editGroupIndex == -1) -1 else state.participantGroups[state.editGroupIndex].groupId,
                                competitionId = if (state.editGroupIndex == -1) -1 else state.participantGroups[state.editGroupIndex].competitionId,
                                title = groupTitle,
                                distance = distance.toDoubleOrNull() ?: 0.0,
                                countOfControls = countOfControls,
                                maxTimeInMinute = maxTime,
                                controlPoints = controlPoints,
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
