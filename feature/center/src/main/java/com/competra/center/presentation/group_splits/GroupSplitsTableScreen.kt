package com.competra.center.presentation.group_splits

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.competra.center.data.results.SplitsTable
import com.competra.center.data.results.SplitsTableCell
import com.competra.center.data.results.SplitsTableRow
import com.competra.designsystem.theme.Dimens
import com.competra.utils.orienteering.toRaceTime
import org.koin.androidx.compose.koinViewModel

private val NAME_COLUMN_WIDTH = 140.dp
private val SPLIT_COLUMN_WIDTH = 76.dp

@Composable
fun GroupSplitsTableScreen(
    groupId: Long,
    competitionId: String,
    viewModel: GroupSplitsTableViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(groupId, competitionId) {
        viewModel.load(groupId, competitionId)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when {
            state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            state.table == null || state.table!!.rows.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Сплиты по группе отсутствуют",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            else -> GroupSplitsTableContent(groupTitle = state.groupTitle, table = state.table!!)
        }
    }
}

@Composable
private fun GroupSplitsTableContent(groupTitle: String, table: SplitsTable) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = groupTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(Dimens.SIZE_BASE.dp),
        )

        val horizontalScrollState = rememberScrollState()

        // Sticky-заголовок: не находится внутри LazyColumn, поэтому не скроллится вертикально.
        Row(modifier = Modifier.fillMaxWidth()) {
            HeaderCell(
                text = "Участник",
                modifier = Modifier.width(NAME_COLUMN_WIDTH),
                textAlign = TextAlign.Start,
            )
            Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                table.columns.forEach { column ->
                    HeaderCell(
                        text = "#${column.positionIndex} (КП${column.controlPoint})",
                        modifier = Modifier.width(SPLIT_COLUMN_WIDTH),
                    )
                }
            }
        }
        HorizontalDivider()

        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(table.rows) { index, row ->
                val rowBackground = if (index % 2 == 0) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowBackground),
                ) {
                    // Sticky первая колонка: находится вне горизонтально скроллящегося Row.
                    ParticipantCell(row = row, modifier = Modifier.width(NAME_COLUMN_WIDTH))
                    Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                        row.cells.forEach { cell ->
                            SplitCell(cell = cell, modifier = Modifier.width(SPLIT_COLUMN_WIDTH))
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun HeaderCell(text: String, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Center) {
    Text(
        text = text,
        modifier = modifier.padding(horizontal = Dimens.SIZE_QUARTER.dp, vertical = Dimens.SIZE_HALF.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = textAlign,
    )
}

@Composable
private fun ParticipantCell(row: SplitsTableRow, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = Dimens.SIZE_HALF.dp, vertical = Dimens.SIZE_QUARTER.dp)) {
        Text(
            text = "${row.participant.lastName} ${row.participant.firstName}",
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
        )
        row.result?.rank?.let { rank ->
            Text(
                text = "Место $rank",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SplitCell(cell: SplitsTableCell, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = Dimens.SIZE_QUARTER.dp, vertical = Dimens.SIZE_QUARTER.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = cell.cumulativeSeconds?.toRaceTime() ?: "—",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = cell.deltaSeconds?.toRaceTime() ?: "",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (cell.isBestLeg) FontWeight.Bold else FontWeight.Normal,
            color = if (cell.isBestLeg) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}
