package com.competra.center.data.results

import com.competra.domain.models.ResultStatus
import com.competra.domain.models.orienteering.GroupWithParticipantsAndResults
import com.competra.domain.models.orienteering.OrienteeringParticipant
import com.competra.domain.models.orienteering.OrienteeringResult
import com.competra.domain.models.orienteering.ParticipantWithResult

/** Колонка таблицы сплитов — один контрольный пункт по позиции в дистанции. */
data class SplitsTableColumn(
    val positionIndex: Int,
    val controlPoint: Int,
)

/** Ячейка таблицы сплитов для одного участника на одном КП. */
data class SplitsTableCell(
    val deltaSeconds: Long?,
    val cumulativeSeconds: Long?,
    val deltaRank: Int?,
    val cumulativeRank: Int?,
    val isBestLeg: Boolean,
)

/** Строка таблицы сплитов — один участник группы. */
data class SplitsTableRow(
    val participant: OrienteeringParticipant,
    val result: OrienteeringResult?,
    val cells: List<SplitsTableCell>,
)

data class SplitsTable(
    val columns: List<SplitsTableColumn>,
    val rows: List<SplitsTableRow>,
)

/**
 * Анкер отсчёта сплитов: фактическое время старта из результата, а если оно потеряно —
 * плановое время старта участника. Тот же анкер использует реконструкция сплитов при
 * HTML-импорте (см. `buildResultsDiff`), поэтому здесь нельзя молча падать на 0 —
 * это рассинхронит отображаемые времена с тем, что реально записано в SplitTime.timestamp.
 */
private fun anchorStartTime(pw: ParticipantWithResult): Long =
    pw.result?.startTime ?: pw.participant.startTime

/**
 * Строит таблицу сплитов группы: сопоставление позиционное (splits[i] <-> cpOrder[i]),
 * что корректно обрабатывает дублирующиеся номера КП в дистанции.
 * Порядок строк — как в [group.participants], сортировку применяет вызывающий код ([sortedForResults]).
 */
fun buildSplitsTable(group: GroupWithParticipantsAndResults): SplitsTable {
    val cpOrder = group.participants
        .mapNotNull { it.result?.splits }
        .maxByOrNull { it.size }
        ?.map { it.controlPoint }
        ?: emptyList()

    val columns = cpOrder.mapIndexed { i, cp -> SplitsTableColumn(positionIndex = i + 1, controlPoint = cp) }

    val cumulRanks: List<Map<String, Int>> = cpOrder.indices.map { i ->
        group.participants
            .mapNotNull { pw ->
                val splits = pw.result?.splits ?: return@mapNotNull null
                val startTs = anchorStartTime(pw)
                if (i < splits.size) pw.participant.id to (splits[i].timestamp - startTs) else null
            }
            .sortedBy { it.second }
            .mapIndexed { rank, (id, _) -> id to (rank + 1) }
            .toMap()
    }

    val deltaRanks: List<Map<String, Int>> = cpOrder.indices.map { i ->
        group.participants
            .mapNotNull { pw ->
                val splits = pw.result?.splits ?: return@mapNotNull null
                val startTs = anchorStartTime(pw)
                if (i < splits.size) {
                    val prevTs = if (i == 0) startTs else splits[i - 1].timestamp
                    pw.participant.id to (splits[i].timestamp - prevTs)
                } else null
            }
            .sortedBy { it.second }
            .mapIndexed { rank, (id, _) -> id to (rank + 1) }
            .toMap()
    }

    val rows = group.participants.map { pw ->
        val splits = pw.result?.splits ?: emptyList()
        val startTs = anchorStartTime(pw)

        val cells = cpOrder.indices.map { i ->
            if (i >= splits.size) {
                SplitsTableCell(
                    deltaSeconds = null,
                    cumulativeSeconds = null,
                    deltaRank = null,
                    cumulativeRank = null,
                    isBestLeg = false,
                )
            } else {
                val splitTs = splits[i].timestamp
                val prevTs = if (i == 0) startTs else splits[i - 1].timestamp
                val cumulSec = (splitTs - startTs) / 1000L
                val deltaSec = (splitTs - prevTs) / 1000L
                val cumulRank = cumulRanks[i][pw.participant.id]
                val deltaRank = deltaRanks[i][pw.participant.id]

                SplitsTableCell(
                    deltaSeconds = deltaSec,
                    cumulativeSeconds = cumulSec,
                    deltaRank = deltaRank,
                    cumulativeRank = cumulRank,
                    isBestLeg = deltaRank == 1,
                )
            }
        }

        SplitsTableRow(participant = pw.participant, result = pw.result, cells = cells)
    }

    return SplitsTable(columns = columns, rows = rows)
}

/** Сортировка участников для отображения результатов: по статусу, затем по итоговому времени. */
fun List<ParticipantWithResult>.sortedForResults(): List<ParticipantWithResult> =
    sortedWith(
        compareBy(
            { p -> statusSortOrder(p.result?.status) },
            { p -> p.result?.totalTime ?: Long.MAX_VALUE },
        )
    )

private fun statusSortOrder(status: ResultStatus?): Int = when (status) {
    ResultStatus.FINISHED -> 0
    ResultStatus.DSQ -> 1
    ResultStatus.DNF -> 2
    ResultStatus.DNS -> 3
    ResultStatus.STARTED -> 4
    ResultStatus.REGISTERED -> 5
    null -> 9
}
