package com.competra.events.presentation.filter

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.competra.designsystem.components.DSBottomDialog
import com.competra.designsystem.components.DSButton
import com.competra.designsystem.theme.Dimens
import com.competra.domain.models.KindOfSport
import com.competra.domain.models.events.EventsFilter
import com.competra.domain.models.orienteering.CompetitionStatus
import com.competra.resources.R
import com.competra.utils.DateTimeFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

/**
 * Нижний диалог с фильтрами списка событий.
 *
 * @param initialFilter Текущий применённый фильтр — становится стартовым состоянием черновика.
 * @param onApply Колбек применения нового фильтра.
 * @param onDismiss Колбек закрытия диалога без применения.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsFilterBottomSheet(
    initialFilter: EventsFilter,
    onApply: (EventsFilter) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var draft by remember(initialFilter) { mutableStateOf(initialFilter) }

    DSBottomDialog(
        sheetState = sheetState,
        onDismiss = onDismiss,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.SIZE_TWO.dp)
            ) {
                Text(
                    text = stringResource(R.string.filter_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = Dimens.SIZE_SINGLE.dp)
                )

                DateSection(
                    dateFrom = draft.dateFrom,
                    dateTo = draft.dateTo,
                    onDateFromChange = { draft = draft.copy(dateFrom = it) },
                    onDateToChange = { draft = draft.copy(dateTo = it) }
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_SINGLE.dp))

                SportSection(
                    selected = draft.kindOfSports,
                    onToggle = { sport ->
                        draft = draft.copy(
                            kindOfSports = draft.kindOfSports.toggle(sport)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_SINGLE.dp))

                StatusSection(
                    selected = draft.statuses,
                    onToggle = { status ->
                        draft = draft.copy(
                            statuses = draft.statuses.toggle(status)
                        )
                    }
                )

                DebugSection(
                    includeTest = draft.includeTest,
                    onIncludeTestChange = { draft = draft.copy(includeTest = it) }
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_TWO.dp))

                DSButton(
                    text = stringResource(R.string.filter_apply),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onApply(draft) }
                )

                Spacer(modifier = Modifier.height(Dimens.SIZE_SINGLE.dp))
            }
        }
    )
}

@Composable
private fun DateSection(
    dateFrom: Long?,
    dateTo: Long?,
    onDateFromChange: (Long?) -> Unit,
    onDateToChange: (Long?) -> Unit
) {
    SectionTitle(text = stringResource(R.string.filter_section_date))
    DatePickerRow(
        label = stringResource(R.string.filter_date_from),
        date = dateFrom,
        onDateChange = onDateFromChange
    )
    DatePickerRow(
        label = stringResource(R.string.filter_date_to),
        date = dateTo,
        onDateChange = onDateToChange
    )
}

@Composable
private fun DatePickerRow(
    label: String,
    date: Long?,
    onDateChange: (Long?) -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val calendar = Calendar.getInstance().also { c ->
                    if (date != null) c.timeInMillis = date
                }
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val millis = LocalDate.of(year, month + 1, dayOfMonth)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
                        onDateChange(millis)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            .padding(vertical = Dimens.SIZE_SINGLE.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = date?.let { DateTimeFormat.transformLongToDisplayDate(it) }
                ?: stringResource(R.string.filter_date_not_set),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (date != null) {
            IconButton(onClick = { onDateChange(null) }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.filter_off),
                    contentDescription = stringResource(R.string.filter_clear_date),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SportSection(
    selected: List<KindOfSport>,
    onToggle: (KindOfSport) -> Unit
) {
    SectionTitle(text = stringResource(R.string.filter_section_sport))
    KindOfSport.all.forEach { sport ->
        CheckboxRow(
            label = stringResource(id = sport.labelRes()),
            checked = selected.any { it.name == sport.name },
            onCheckedChange = { onToggle(sport) }
        )
    }
}

@Composable
private fun StatusSection(
    selected: List<CompetitionStatus>,
    onToggle: (CompetitionStatus) -> Unit
) {
    SectionTitle(text = stringResource(R.string.filter_section_status))
    CheckboxRow(
        label = stringResource(R.string.filter_status_registration_open),
        checked = CompetitionStatus.REGISTRATION_OPEN in selected,
        onCheckedChange = { onToggle(CompetitionStatus.REGISTRATION_OPEN) }
    )
    CheckboxRow(
        label = stringResource(R.string.filter_status_in_progress),
        checked = CompetitionStatus.IN_PROGRESS in selected,
        onCheckedChange = { onToggle(CompetitionStatus.IN_PROGRESS) }
    )
    CheckboxRow(
        label = stringResource(R.string.filter_status_finished),
        checked = CompetitionStatus.FINISHED in selected,
        onCheckedChange = { onToggle(CompetitionStatus.FINISHED) }
    )
}

/**
 * Debug-секция фильтра. Видна только в debug-сборке (флаг
 * [android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE]). Содержит переключатель
 * «Показывать тестовые», который выставляет [EventsFilter.includeTest] —
 * на сервере по нему в публичную ленту попадают и тестовые соревнования.
 * В релизной сборке секция не отрисовывается.
 */
@Composable
private fun DebugSection(
    includeTest: Boolean,
    onIncludeTestChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val isDebuggable = remember {
        (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
    if (!isDebuggable) return

    SectionTitle(text = "DEBUG")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onIncludeTestChange(!includeTest) }
            .padding(vertical = Dimens.SIZE_SINGLE.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Показывать тестовые",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Включить в публичный список тестовые соревнования",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = includeTest,
            onCheckedChange = onIncludeTestChange
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = Dimens.SIZE_SINGLE.dp, bottom = 4.dp)
    )
}

@Composable
private fun CheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(checked = checked, onCheckedChange = { onCheckedChange() })
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

private fun KindOfSport.labelRes(): Int = when (this) {
    KindOfSport.Orienteering -> R.string.filter_sport_orienteering
    KindOfSport.CrossCountrySki -> R.string.filter_sport_cross_country_ski
    KindOfSport.TrailRunning -> R.string.filter_sport_trail_running
}

private fun List<KindOfSport>.toggle(item: KindOfSport): List<KindOfSport> =
    if (any { it.name == item.name }) filterNot { it.name == item.name } else this + item

@JvmName("toggleStatus")
private fun List<CompetitionStatus>.toggle(item: CompetitionStatus): List<CompetitionStatus> =
    if (item in this) this - item else this + item
