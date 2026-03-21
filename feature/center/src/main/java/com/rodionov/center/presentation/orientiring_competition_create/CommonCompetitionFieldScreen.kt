package com.rodionov.center.presentation.orientiring_competition_create

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.components.TimePickerDialog
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.center.data.creator.OrienteeringCreatorState
import com.rodionov.resources.R
import com.rodionov.utils.DateTimeFormat
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

/**
 * Первый экран создания соревнования: Общая информация.
 * 
 * Включает ввод названия, дат начала (в том числе для многодневных этапов), 
 * адреса, координат места старта и описания.
 * 
 * @param competitionId Идентификатор редактируемого соревнования.
 * @param viewModel Вьюмодель процесса создания.
 */
@Composable
fun CommonCompetitionFieldScreen(
    competitionId: Long? = null,
    viewModel: OrienteeringCreatorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.initialize(competitionId)
    }

    Scaffold(
        bottomBar = {
            NavigationButtons(
                onBack = { 
                    viewModel.back()
                },
                onNext = { viewModel.saveStepOne() },
                nextEnabled = state.title.isNotBlank() && state.address.isNotBlank()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.SIZE_BASE.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Шаг 1: Общая информация",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Поле ввода названия
            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.title,
                label = { Text("Название соревнования") },
                onValueChanged = viewModel::updateTitle
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Поля даты и времени начала первого дня
            Text(
                text = "Начало соревнования",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    DatePicker(state = state, userAction = viewModel::onAction)
                }
                Spacer(modifier = Modifier.width(Dimens.SIZE_HALF.dp))
                Box(modifier = Modifier.weight(1f)) {
                    TimePicker(state = state, userAction = viewModel::onAction)
                }
            }

            // Дополнительные этапы (многодневки)
            if (state.stages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
                Text(
                    text = "Дополнительные дни",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                state.stages.forEachIndexed { index, stage ->
                    Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            StageDatePicker(index = index, date = stage.startDate, userAction = viewModel::onAction)
                        }
                        Spacer(modifier = Modifier.width(Dimens.SIZE_HALF.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            StageTimePicker(index = index, date = stage.startDate, userAction = viewModel::onAction)
                        }
                        IconButton(onClick = { viewModel.onAction(OrienteeringCreatorAction.RemoveStage(index)) }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.delete),
                                contentDescription = "Remove stage",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            TextButton(
                onClick = { viewModel.onAction(OrienteeringCreatorAction.AddStage) },
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(ImageVector.vectorResource(R.drawable.ic_add_24px), contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Добавить день")
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Поле ввода адреса
            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.address,
                label = { Text("Место проведения (адрес)") },
                onValueChanged = viewModel::updateAddress
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            // Выбор координат на карте
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.SIZE_HALF.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* TODO: Открыть карту */ },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.map_24dp),
                        contentDescription = "Open map",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.SIZE_BASE.dp))
                Column {
                    Text(text = "Координаты старта", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "lat: ${"%.5f".format(state.coordinates.latitude)}, lon: ${"%.5f".format(state.coordinates.longitude)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Поле ввода описания
            DSTextInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                text = state.description,
                label = { Text("Описание соревнования") },
                onValueChanged = viewModel::updateDescription,
                singleLine = false
            )
            
            Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))
        }
    }
}

@Composable
fun NavigationButtons(
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextEnabled: Boolean = true,
    nextText: String = "Далее"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.SIZE_BASE.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(onClick = onBack) {
            Text("Назад")
        }
        Button(onClick = onNext, enabled = nextEnabled) {
            Text(nextText)
        }
    }
}

/**
 * Компонент выбора даты (Stage).
 */
@Composable
private fun StageDatePicker(index: Int, date: Long, userAction: (OrienteeringCreatorAction) -> Unit) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val focusManager = LocalFocusManager.current

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val dateMillis = LocalDate.of(year, month + 1, dayOfMonth)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
                userAction.invoke(OrienteeringCreatorAction.UpdateStageDate(index, dateMillis))
                focusManager.clearFocus()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    DSTextInput(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { if (it.isFocused) datePickerDialog.show() },
        label = { Text(text = "Дата") },
        text = DateTimeFormat.transformLongToDisplayDate(date),
        readOnly = true
    )
}

/**
 * Компонент выбора времени (Stage).
 */
@Composable
private fun StageTimePicker(index: Int, date: Long, userAction: (OrienteeringCreatorAction) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    DSTextInput(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { showDialog = it.isFocused },
        label = { Text(text = "Время") },
        text = DateTimeFormat.transformLongToTime(date),
        readOnly = true
    )

    if (showDialog) {
        TimePickerDialog(
            onDismissRequest = {
                showDialog = false
                focusManager.clearFocus()
            },
            onConfirm = { hour, minute ->
                userAction.invoke(
                    OrienteeringCreatorAction.UpdateStageTime(index, "%02d:%02d".format(hour, minute))
                )
                showDialog = false
                focusManager.clearFocus()
            }
        )
    }
}
