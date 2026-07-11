package com.competra.domain.models.orienteering

import com.competra.domain.models.ResultStatus
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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
    val paceMinPerKm: Double? = null,
)

private const val EARTH_RADIUS_METERS = 6_371_000.0

/**
 * Расстояние между двумя контрольными пунктами по их WGS84-координатам, в метрах.
 * Null, если у одного из КП нет координат (дистанция не импортирована из геопривязанной карты
 * или создана вручную). Переиспользуется как таблицей сплитов, так и экраном сканирования чипа
 * (feature:center) — единая формула для обоих мест отображения темпа.
 */
fun controlPointDistanceMeters(from: ControlPoint?, to: ControlPoint?): Double? {
    val lat1 = from?.latitude ?: return null
    val lon1 = from.longitude ?: return null
    val lat2 = to?.latitude ?: return null
    val lon2 = to?.longitude ?: return null
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
    return EARTH_RADIUS_METERS * 2 * atan2(sqrt(a), sqrt(1 - a))
}

/** Темп участника на перегоне в минутах на километр, либо null, если длина перегона неизвестна/нулевая. */
fun paceMinPerKm(deltaSeconds: Long, legLengthMeters: Double?): Double? {
    if (legLengthMeters == null || legLengthMeters <= 0) return null
    return (deltaSeconds / 60.0) / (legLengthMeters / 1000.0)
}

/**
 * Длина перегона (в метрах) для каждой позиции в [cpOrder] дистанции [distance]: null для первой
 * позиции (нет координаты старта до первого КП) и там, где у одного из соседних КП нет координат.
 */
private fun legLengthsMeters(distance: Distance?, cpOrder: List<Int>): List<Double?> {
    val expected = distance?.expectedSequence() ?: return List(cpOrder.size) { null }
    return cpOrder.indices.map { i ->
        if (i == 0) return@map null
        controlPointDistanceMeters(expected.getOrNull(i - 1), expected.getOrNull(i))
    }
}

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
 *
 * @param distance дистанция группы — если передана и у КП есть координаты (импорт из
 * геопривязанной карты через IOF XML), в ячейках заполняется темп участника на перегоне
 * ([SplitsTableCell.paceMinPerKm]); без неё темп остаётся null.
 */
fun buildSplitsTable(group: GroupWithParticipantsAndResults, distance: Distance? = null): SplitsTable {
    val cpOrder = group.participants
        .mapNotNull { it.result?.splits }
        .maxByOrNull { it.size }
        ?.map { it.controlPoint }
        ?: emptyList()

    val columns = cpOrder.mapIndexed { i, cp -> SplitsTableColumn(positionIndex = i + 1, controlPoint = cp) }
    val legLengths = legLengthsMeters(distance, cpOrder)

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
                val pace = paceMinPerKm(deltaSec, legLengths.getOrNull(i))

                SplitsTableCell(
                    deltaSeconds = deltaSec,
                    cumulativeSeconds = cumulSec,
                    deltaRank = deltaRank,
                    paceMinPerKm = pace,
                    cumulativeRank = cumulRank,
                    isBestLeg = deltaRank == 1,
                )
            }
        }

        SplitsTableRow(participant = pw.participant, result = pw.result, cells = cells)
    }

    return SplitsTable(columns = columns, rows = rows)
}

/**
 * Сортировка участников для отображения результатов: по статусу, затем —
 * для BY_CHOICE (score-О) по сумме баллов убыв. с тай-брейком по времени финиша,
 * для остальных направлений — по итоговому времени возрастанию (как раньше).
 */
fun List<ParticipantWithResult>.sortedForResults(
    direction: OrienteeringDirection = OrienteeringDirection.FORWARD
): List<ParticipantWithResult> =
    if (direction == OrienteeringDirection.BY_CHOICE) {
        sortedWith(
            compareBy<ParticipantWithResult> { statusSortOrder(it.result?.status) }
                .thenByDescending { it.result?.totalScore ?: 0 }
                .thenBy { it.result?.finishTime ?: Long.MAX_VALUE }
        )
    } else {
        sortedWith(
            compareBy(
                { p -> statusSortOrder(p.result?.status) },
                { p -> p.result?.totalTime ?: Long.MAX_VALUE },
            )
        )
    }

private fun statusSortOrder(status: ResultStatus?): Int = when (status) {
    ResultStatus.FINISHED -> 0
    ResultStatus.DSQ -> 1
    ResultStatus.DNF -> 2
    ResultStatus.DNS -> 3
    ResultStatus.STARTED -> 4
    ResultStatus.REGISTERED -> 5
    null -> 9
}

/** Точка графика сплитов для одного КП: отставание участника от лидера в секундах. */
data class RaceGraphPoint(
    val positionIndex: Int,
    val controlPoint: Int,
    val deltaSeconds: Long?,
)

/** Кривая отставания от лидера для одного участника по всем КП дистанции. */
data class RaceGraphSeries(
    val participant: OrienteeringParticipant,
    val result: OrienteeringResult?,
    val points: List<RaceGraphPoint>,
)

data class RaceGraphData(
    val columns: List<SplitsTableColumn>,
    val series: List<RaceGraphSeries>,
)

/**
 * Строит данные для графика отставания от лидера (race graph, аналог WinSplits) на основе
 * уже посчитанной [SplitsTable]: для каждого КП лидер — участник с минимальным
 * [SplitsTableCell.cumulativeSeconds], отставание остальных считается относительно него.
 * Не финишировавшие (DNS/DNF/DSQ/REGISTERED/STARTED) исключаются — их кривая не имеет смысла.
 */
fun buildRaceGraphData(table: SplitsTable): RaceGraphData {
    val finishedRows = table.rows.filter { it.result?.status == ResultStatus.FINISHED }

    val leaderCumulativeByColumn = table.columns.indices.map { i ->
        finishedRows.mapNotNull { it.cells.getOrNull(i)?.cumulativeSeconds }.minOrNull()
    }

    val series = finishedRows.map { row ->
        val points = table.columns.mapIndexed { i, column ->
            val cumulative = row.cells.getOrNull(i)?.cumulativeSeconds
            val leader = leaderCumulativeByColumn[i]
            RaceGraphPoint(
                positionIndex = column.positionIndex,
                controlPoint = column.controlPoint,
                deltaSeconds = if (cumulative != null && leader != null) cumulative - leader else null,
            )
        }
        RaceGraphSeries(participant = row.participant, result = row.result, points = points)
    }

    return RaceGraphData(columns = table.columns, series = series)
}
