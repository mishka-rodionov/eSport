package com.competra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.competra.designsystem.theme.Dimens
import com.competra.domain.models.orienteering.ScoreGraphData
import com.competra.domain.models.orienteering.ScoreGraphSeries
import com.competra.utils.orienteering.toRaceTime
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill

/**
 * График набора очков во времени (BY_CHOICE, score-О): X — время от старта участника, Y —
 * накопленные очки за фактически взятые КП. Аналог [RaceGraphChart] по устройству (та же
 * палитра [raceGraphPalette], тот же принцип легенды-переключателя видимости/подсветки), но
 * ось X непрерывная по времени, а не по позиции общего для всех КП — для BY_CHOICE общего
 * порядка КП не существует.
 */
@Composable
fun ScoreGraphChart(
    data: ScoreGraphData,
    visibleParticipantIds: Set<String>,
    highlightedParticipantId: String?,
    onToggleVisibility: (participantId: String) -> Unit,
    onToggleHighlight: (participantId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorByParticipantId = remember(data) {
        data.series.mapIndexed { index, series ->
            series.participant.id to raceGraphPalette[index % raceGraphPalette.size]
        }.toMap()
    }

    val visibleSeries = remember(data, visibleParticipantIds) {
        data.series.filter { it.participant.id in visibleParticipantIds }
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(visibleSeries) {
        if (visibleSeries.isEmpty()) return@LaunchedEffect
        modelProducer.runTransaction {
            lineModel {
                visibleSeries.forEach { s ->
                    if (s.points.isNotEmpty()) {
                        series(
                            x = s.points.map { it.elapsedSeconds },
                            y = s.points.map { it.cumulativeScore.toDouble() },
                            key = s.participant.id,
                        )
                    }
                }
            }
        }
    }

    Column(modifier = modifier) {
        if (visibleSeries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(240.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Нет участников для отображения на графике",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            val lines = visibleSeries.map { s ->
                val baseColor = colorByParticipantId[s.participant.id] ?: Color.Gray
                val isDimmed = highlightedParticipantId != null && highlightedParticipantId != s.participant.id
                LineCartesianLayer.rememberLine(
                    fill = LineCartesianLayer.LineFill.single(
                        Fill(if (isDimmed) baseColor.copy(alpha = 0.25f) else baseColor)
                    ),
                )
            }

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(lines),
                    ),
                    startAxis = VerticalAxis.rememberStart(
                        valueFormatter = CartesianValueFormatter { _, value, _ -> value.toInt().toString() },
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = CartesianValueFormatter { _, value, _ -> value.toLong().toRaceTime() },
                    ),
                ),
                modelProducer = modelProducer,
                modifier = Modifier.fillMaxWidth().height(280.dp),
            )
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(data.series, key = { it.participant.id }) { series ->
                ScoreGraphLegendRow(
                    series = series,
                    color = colorByParticipantId[series.participant.id] ?: Color.Gray,
                    isVisible = series.participant.id in visibleParticipantIds,
                    isHighlighted = series.participant.id == highlightedParticipantId,
                    onToggleVisibility = { onToggleVisibility(series.participant.id) },
                    onToggleHighlight = { onToggleHighlight(series.participant.id) },
                )
            }
        }
    }
}

@Composable
private fun ScoreGraphLegendRow(
    series: ScoreGraphSeries,
    color: Color,
    isVisible: Boolean,
    isHighlighted: Boolean,
    onToggleVisibility: () -> Unit,
    onToggleHighlight: () -> Unit,
) {
    val rowBackground = if (isHighlighted) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBackground)
            .clickable(enabled = isVisible, onClick = onToggleHighlight)
            .padding(horizontal = Dimens.SIZE_BASE.dp, vertical = Dimens.SIZE_QUARTER.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = isVisible, onCheckedChange = { onToggleVisibility() })

        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(if (isVisible) color else MaterialTheme.colorScheme.outlineVariant),
        )

        Column(modifier = Modifier.weight(1f).padding(start = Dimens.SIZE_HALF.dp)) {
            Text(
                text = "${series.participant.lastName} ${series.participant.firstName}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
            )
            series.result?.rank?.let { rank ->
                Text(
                    text = "Место $rank",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
