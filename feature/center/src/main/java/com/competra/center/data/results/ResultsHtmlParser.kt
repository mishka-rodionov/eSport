package com.competra.center.data.results

import com.competra.domain.models.ResultStatus
import com.competra.domain.models.orienteering.GroupWithParticipantsAndResults
import com.competra.domain.models.orienteering.OrienteeringParticipant
import com.competra.domain.models.orienteering.OrienteeringResult
import com.competra.domain.models.orienteering.SplitTime
import com.competra.utils.orienteering.toRaceTime
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

/**
 * Разбирает HTML-протокол результатов (тот же формат, что генерирует buildHtmlContent() в
 * OrienteeringCompetitionResultsViewModel: table.rezult + якоря <a name="..."> для групп) и
 * сопоставляет его с текущими участниками/результатами соревнования — восстановление из бэкапа.
 */

/** Одна строка результата, распознанная в HTML-протоколе (ещё не сопоставленная с участником). */
data class ParsedResultRow(
    val groupTitle: String,
    val startNumber: String,
    val fullName: String,
    val totalTimeSeconds: Long?,
    val status: ResultStatus?,
    val rank: Int?,
    /** Контрольный пункт -> кумулятивное время от старта в секундах (null = участник его не прошёл). */
    val splits: List<Pair<Int, Long?>>,
)

/** Одна изменившаяся и заматченная строка импорта, готовая к показу в превью и к сохранению. */
data class ImportResultRow(
    val participant: OrienteeringParticipant,
    val existing: OrienteeringResult?,
    val request: OrienteeringResult,
    val changeSummary: String,
)

data class ImportResultsDiff(
    val changed: List<ImportResultRow>,
    val unmatched: List<ParsedResultRow>,
)

private val raceTimeRegex = Regex("""^(\d+):(\d{2}):(\d{2})$""")
private val splitCellRegex = Regex("""^(\d+):(\d{2}):(\d{2})\(\d+\)$""")
private val cpHeaderRegex = Regex("""\((\d+)\)""")

private fun parseRaceTimeSeconds(text: String): Long? {
    val m = raceTimeRegex.matchEntire(text.trim()) ?: return null
    val (h, mi, s) = m.destructured
    return h.toLong() * 3600 + mi.toLong() * 60 + s.toLong()
}

private fun parseSplitCumulSeconds(text: String): Long? {
    val m = splitCellRegex.matchEntire(text.trim()) ?: return null
    val (h, mi, s) = m.destructured
    return h.toLong() * 3600 + mi.toLong() * 60 + s.toLong()
}

/** Обратное преобразование statusText из buildHtmlContent() (строки 461-467). */
private fun parseResultStatus(text: String): Pair<ResultStatus?, Long?> = when {
    text.isBlank() -> null to null
    text == "снят" -> ResultStatus.DSQ to null
    text == "н/с" -> ResultStatus.DNS to null
    text == "не финишировал" -> ResultStatus.DNF to null
    else -> parseRaceTimeSeconds(text)?.let { ResultStatus.FINISHED to it } ?: (null to null)
}

/** Кумулятивная часть ячейки КП: `<td>H:MM:SS(rank)<br>MM:SS(rank)</td>` — берём фрагмент до `<br>`. */
private fun firstBrFragmentText(td: Element): String {
    val html = td.html()
    val first = html.split(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE)).firstOrNull() ?: ""
    return Jsoup.parse(first).text().trim()
}

/** Поднимается по предыдущим соседям таблицы до ближайшего `<a name="...">` — якоря группы. */
private fun findGroupTitle(table: Element): String {
    var el = table.previousElementSibling()
    while (el != null) {
        if (el.tagName().equals("a", ignoreCase = true) && el.hasAttr("name")) {
            return el.attr("name")
        }
        el = el.previousElementSibling()
    }
    return ""
}

/** Парсит HTML-протокол результатов в плоский список строк, без сопоставления с участниками. */
fun parseResultsHtml(html: String): List<ParsedResultRow> {
    val doc = Jsoup.parse(html)
    val tables = doc.select("table.rezult")
    val rows = mutableListOf<ParsedResultRow>()

    for (table in tables) {
        val groupTitle = findGroupTitle(table)
        val trs = table.select("tr")
        if (trs.isEmpty()) continue

        val headerThs = trs.first()!!.select("th")
        val cpNumbers = headerThs.drop(7).map { th ->
            cpHeaderRegex.find(th.text())?.groupValues?.get(1)?.toIntOrNull() ?: 0
        }

        for (tr in trs.drop(1)) {
            val tds = tr.select("td")
            if (tds.size < 7) continue

            val startNumber = tds[1].text().trim()
            if (startNumber.isBlank()) continue
            val fullName = tds[2].text().trim()
            val (status, totalTimeSeconds) = parseResultStatus(tds[4].text().trim())
            val rank = tds[5].text().trim().toIntOrNull()

            val splitCells = tds.drop(7)
            val splits = splitCells.mapIndexed { i, td ->
                val cp = cpNumbers.getOrElse(i) { 0 }
                cp to parseSplitCumulSeconds(firstBrFragmentText(td))
            }

            rows += ParsedResultRow(
                groupTitle = groupTitle,
                startNumber = startNumber,
                fullName = fullName,
                totalTimeSeconds = totalTimeSeconds,
                status = status,
                rank = rank,
                splits = splits,
            )
        }
    }

    return rows
}

/**
 * Сопоставляет распарсенные строки с текущими участниками (по startNumber — номер уникален
 * в рамках всего соревнования) и текущими результатами, оставляя только реально изменившиеся строки.
 */
fun buildResultsDiff(
    parsedRows: List<ParsedResultRow>,
    groups: List<GroupWithParticipantsAndResults>,
    competitionId: String,
): ImportResultsDiff {
    val participantsByNumber = groups
        .flatMap { it.participants }
        .associateBy({ it.participant.startNumber }, { it })

    val changed = mutableListOf<ImportResultRow>()
    val unmatched = mutableListOf<ParsedResultRow>()

    parsedRows.forEach { row ->
        val participantWithResult = participantsByNumber[row.startNumber]
        if (participantWithResult == null) {
            unmatched += row
            return@forEach
        }

        val participant = participantWithResult.participant
        val existing = participantWithResult.result

        val newSplits = existing?.startTime?.let { startTime ->
            row.splits
                .mapNotNull { (cp, cumulSec) -> cumulSec?.let { SplitTime(cp, startTime + it * 1000) } }
                .takeIf { it.isNotEmpty() }
        }

        val newStatus = row.status ?: existing?.status ?: ResultStatus.FINISHED
        val newTotalTime = row.totalTimeSeconds ?: existing?.totalTime
        val newRank = row.rank ?: existing?.rank

        val isChanged = existing == null ||
            existing.totalTime != newTotalTime ||
            existing.rank != newRank ||
            existing.status != newStatus ||
            !splitsEqual(existing.splits, newSplits)

        if (!isChanged) return@forEach

        val request = OrienteeringResult(
            id = existing?.id ?: 0,
            competitionId = competitionId,
            groupId = participant.groupId,
            participantId = participant.id,
            startTime = existing?.startTime,
            finishTime = existing?.finishTime,
            totalTime = newTotalTime,
            rank = newRank,
            status = newStatus,
            penaltyTime = existing?.penaltyTime ?: 0,
            splits = newSplits,
            isEditable = existing?.isEditable ?: true,
            isEdited = true,
            remoteId = existing?.remoteId,
            serverUpdatedAt = existing?.serverUpdatedAt,
        )

        val groupMismatch = row.groupTitle.isNotBlank() && row.groupTitle != participant.groupName

        changed += ImportResultRow(
            participant = participant,
            existing = existing,
            request = request,
            changeSummary = buildChangeSummary(existing, request, groupMismatch),
        )
    }

    return ImportResultsDiff(changed, unmatched)
}

private fun splitsEqual(a: List<SplitTime>?, b: List<SplitTime>?): Boolean =
    a.orEmpty().sortedBy { it.controlPoint } == b.orEmpty().sortedBy { it.controlPoint }

private fun buildChangeSummary(existing: OrienteeringResult?, request: OrienteeringResult, groupMismatch: Boolean): String {
    val parts = mutableListOf<String>()
    if (groupMismatch) {
        parts += "⚠ группа в файле не совпадает с текущей группой участника"
    }
    if (existing?.rank != request.rank) {
        parts += "Место: ${existing?.rank ?: "—"} → ${request.rank ?: "—"}"
    }
    if (existing?.totalTime != request.totalTime) {
        val oldT = existing?.totalTime?.toRaceTime() ?: "—"
        val newT = request.totalTime?.toRaceTime() ?: "—"
        parts += "Время: $oldT → $newT"
    }
    if (existing?.status != request.status) {
        parts += "Статус: ${existing?.status?.name ?: "—"} → ${request.status.name}"
    }
    val oldSplitsCount = existing?.splits?.size ?: 0
    val newSplitsCount = request.splits?.size ?: 0
    if (oldSplitsCount != newSplitsCount) {
        parts += "КП: $oldSplitsCount → $newSplitsCount"
    }
    return parts.joinToString("; ").ifEmpty { "Без изменений" }
}
