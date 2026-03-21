package com.rodionov.center.presentation.orientiring_competition_create

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.components.DSTextInput
import com.example.designsystem.components.ExposedDropdownMenuOutlined
import com.example.designsystem.components.TimePickerDialog
import com.example.designsystem.theme.Dimens
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.center.data.creator.OrienteeringCreatorState
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.StartTimeMode
import com.rodionov.resources.R
import com.rodionov.utils.DateTimeFormat
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

/**
 * Экран создания соревнования по ориентированию.
 */
@Composable
fun OrienteeringCompetitionCreator(
    competitionId: Long? = null,
    viewModel: OrienteeringCreatorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.initialize(competitionId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(Dimens.SIZE_BASE.dp)
        ) {
            Text(
                text = if (competitionId == null) "Новое соревнование" else "Редактирование",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Основная информация
            SectionHeader(title = "Основная информация")
            
            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.title,
                label = { Text(text = stringResource(R.string.label_competition_name)) },
                onValueChanged = viewModel::updateTitle
            )
            
            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))

            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.label_competition_place)) },
                isError = state.errors.isEmptyAddress,
                supportingText = {
                    if (state.errors.isEmptyAddress) {
                        Text(text = stringResource(R.string.error_competition_place))
                    }
                },
                text = state.address,
                onValueChanged = viewModel::updateAddress
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

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Настройки дисциплины
            SectionHeader(title = "Настройки дисциплины")
            
            OrienteeringCompetitionDirection(state = state, userAction = viewModel::onAction)
            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
            StartTimeModeSelector(state = state, viewModel = viewModel, userAction = viewModel::onAction)

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Описание
            SectionHeader(title = "Дополнительно")
            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                onValueChanged = viewModel::updateDescription,
                text = state.description,
                label = { Text(text = stringResource(R.string.label_competition_description)) },
                placeholder = { Text(text = stringResource(R.string.hint_competition_description)) }
            )

            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))

            // Группы участников
            ParticipantGroupContent(state = state, userAction = viewModel::onAction)

            Spacer(modifier = Modifier.height(Dimens.SIZE_DOUBLE.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = { viewModel.onAction(OrienteeringCreatorAction.Apply) },
                shape = RoundedCornerShape(Dimens.SIZE_BASE.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_apply),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.SIZE_BASE.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = Dimens.SIZE_HALF.dp)
    )
}

@Composable
private fun ParticipantGroupContent(
    state: OrienteeringCreatorState,
    userAction: (OrienteeringCreatorAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionHeader(title = stringResource(R.string.label_groups))
        
        TextButton(onClick = { userAction.invoke(OrienteeringCreatorAction.ShowGroupCreateDialog) }) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_add_24px),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Добавить")
        }
    }

    if (state.errors.isEmptyGroup) {
        Text(
            text = stringResource(R.string.label_add_at_least_group),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = Dimens.SIZE_HALF.dp)
        )
    }

    if (state.participantGroups.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(Dimens.SIZE_BASE.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SIZE_BASE.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Список групп пуст",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyRow(
            contentPadding = PaddingValues(vertical = Dimens.SIZE_HALF.dp),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SIZE_BASE.dp)
        ) {
            itemsIndexed(state.participantGroups) { index, item ->
                GroupCard(item, userAction, index)
            }
        }
    }

    if (state.isShowGroupCreateDialog) {
        ParticipantGroupEditor(
            userAction = userAction,
            state = state
        )
    }
}

@Composable
fun GroupCard(
    participantGroup: ParticipantGroup,
    userAction: (OrienteeringCreatorAction) -> Unit,
    groupIndex: Int
) {
    Card(
        modifier = Modifier
            .width(200.dp),
        shape = RoundedCornerShape(Dimens.SIZE_BASE.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.SIZE_BASE.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = participantGroup.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row {
                    IconButton(
                        onClick = { userAction.invoke(OrienteeringCreatorAction.EditGroupDialog(groupIndex)) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.edit),
                            contentDescription = "edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { userAction.invoke(OrienteeringCreatorAction.DeleteGroup(index = groupIndex)) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.delete),
                            contentDescription = "delete",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
            
            GroupInfoRow(label = "Дистанция", value = "Брать из модели дистанции м")
            GroupInfoRow(label = "КП", value = "Брать из модели дистанции")
            GroupInfoRow(label = "Лимит", value = "Брать из модели дистанции мин")
        }
    }
}

@Composable
private fun GroupInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

/**
 * Компонент выбора даты.
 */
@Composable
fun DatePicker(state: OrienteeringCreatorState, userAction: (OrienteeringCreatorAction) -> Unit) {
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
                userAction.invoke(OrienteeringCreatorAction.UpdateCompetitionDate(dateMillis))
                focusManager.clearFocus()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    datePickerDialog.setOnDismissListener { focusManager.clearFocus() }

    DSTextInput(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { if (it.isFocused) datePickerDialog.show() },
        label = { Text(text = stringResource(R.string.label_date)) },
        text = DateTimeFormat.transformLongToDisplayDate(state.startDate),
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_date_range_24px),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}

/**
 * Компонент выбора времени.
 */
@Composable
fun TimePicker(state: OrienteeringCreatorState, userAction: (OrienteeringCreatorAction) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    DSTextInput(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { showDialog = it.isFocused },
        label = { Text(text = stringResource(R.string.label_time)) },
        text = DateTimeFormat.transformLongToTime(state.startDate),
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_build_24px), // Используем как заглушку для времени
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )

    if (showDialog) {
        TimePickerDialog(
            onDismissRequest = {
                showDialog = false
                focusManager.clearFocus()
            },
            onConfirm = { hour, minute ->
                userAction.invoke(
                    OrienteeringCreatorAction.UpdateCompetitionTime("%02d:%02d".format(hour, minute))
                )
                showDialog = false
                focusManager.clearFocus()
            }
        )
    }
}

@Composable
private fun OrienteeringCompetitionDirection(
    state: OrienteeringCreatorState,
    userAction: (OrienteeringCreatorAction) -> Unit
) {
    val context = LocalContext.current
    ExposedDropdownMenuOutlined(
        label = stringResource(R.string.label_direction),
        items = OrienteeringDirection.entries,
        selectedItem = state.competitionDirection,
        onItemSelected = {
            userAction.invoke(OrienteeringCreatorAction.UpdateCompetitionDirection(it))
        },
        itemToString = {
            when(it) {
                OrienteeringDirection.FORWARD -> context.getString(R.string.label_direction_forward)
                OrienteeringDirection.BY_CHOICE -> context.getString(R.string.label_direction_by_choice)
                OrienteeringDirection.MARKING -> context.getString(R.string.label_direction_marking)
            }
        }
    )
}

/**
 * Компонент выбора режима времени старта.
 */
@Composable
private fun StartTimeModeSelector(
    state: OrienteeringCreatorState,
    viewModel: OrienteeringCreatorViewModel,
    userAction: (OrienteeringCreatorAction) -> Unit
) {
    val context = LocalContext.current
    Column {
        ExposedDropdownMenuOutlined(
            label = stringResource(R.string.label_start_time_mode),
            items = StartTimeMode.entries,
            selectedItem = state.startTimeMode,
            onItemSelected = {
                userAction.invoke(OrienteeringCreatorAction.UpdateStartTimeMode(it))
            },
            itemToString = {
                when (it) {
                    StartTimeMode.STRICT -> context.getString(R.string.label_start_time_mode_strict)
                    StartTimeMode.USER_SET -> context.getString(R.string.label_start_time_mode_user_set)
                    StartTimeMode.BY_START_STATION -> context.getString(R.string.label_start_time_mode_by_start_station)
                }
            }
        )
        if (state.startTimeMode == StartTimeMode.USER_SET) {
            Spacer(modifier = Modifier.height(Dimens.SIZE_HALF.dp))
            DSTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = state.countdownTimer?.toString() ?: "",
//                onValueChanged = viewModel::updateCountdownTimer,
                onValueChanged = {},
                label = { Text(text = "Таймер отсчета (мин)") }
            )
        }
    }
}
