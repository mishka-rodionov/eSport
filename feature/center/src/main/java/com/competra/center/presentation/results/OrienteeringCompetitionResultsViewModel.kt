package com.competra.center.presentation.results

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.lifecycle.viewModelScope
import com.competra.center.data.interactors.OrienteeringCompetitionInteractor
import com.competra.center.data.results.OrienteeringCompetitionResultsState
import com.competra.data.navigation.CenterNavigation
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.getArguments
import com.competra.domain.models.ResultStatus
import com.competra.domain.models.orienteering.GroupWithParticipantsAndResults
import com.competra.domain.models.orienteering.OrienteeringResult
import com.competra.domain.models.orienteering.ParticipantWithResult
import com.competra.domain.repository.UploadRepository
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import com.competra.utils.DateTimeFormat
import com.competra.utils.constants.EventsConstants
import com.competra.utils.orienteering.toRaceTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

/**
 * ViewModel для экрана результатов соревнований по ориентированию.
 * Управляет состоянием экрана, загрузкой данных и обработкой действий пользователя.
 *
 * @param orienteeringCompetitionInteractor Интерактор для работы с данными соревнований.
 * @param navigation Навигация для перехода между экранами.
 */
class OrienteeringCompetitionResultsViewModel(
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor,
    private val navigation: Navigation,
    private val uploadRepository: UploadRepository
): BaseViewModel<OrienteeringCompetitionResultsState>(OrienteeringCompetitionResultsState()) {

    val competitionId: String? = navigation.getArguments<String>(EventsConstants.EVENT_ID.name)

    private val _exportCsvEvent = MutableSharedFlow<Pair<String, String>>()
    val exportCsvEvent: SharedFlow<Pair<String, String>> = _exportCsvEvent

    private val _exportPdfEvent = MutableSharedFlow<Pair<String, ByteArray>>()
    val exportPdfEvent: SharedFlow<Pair<String, ByteArray>> = _exportPdfEvent


    init {
        loadResults()
    }

    /**
     * Загружает результаты соревнований и обновляет состояние.
     */
    private fun loadResults() {
        competitionId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val competition = orienteeringCompetitionInteractor.getCompetition(it)
                val results = orienteeringCompetitionInteractor.getResultsByGroups(it).getOrNull() ?: emptyList()
                val sortedResults = results.map { group ->
                    group.copy(
                        participants = group.participants.sortedWith(
                            compareBy(
                                { p -> statusSortOrder(p.result?.status) },
                                { p -> p.result?.totalTime ?: Long.MAX_VALUE }
                            )
                        )
                    )
                }
                val isApproved = sortedResults.isNotEmpty() &&
                    sortedResults.all { group -> group.participants.all { it.result?.isEditable == false } }
                updateState {
                    copy(
                        groupsWithParticipantsAndResults = sortedResults,
                        isApproved = isApproved,
                        competitionTitle = competition?.competition?.title ?: "",
                    )
                }
            }
        }
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

    override fun onAction(action: BaseAction) {
        when (action) {
            is OrienteeringResultsAction.UpdateResult -> updateParticipantResult(action.participantWithResult, action.startTime, action.finishTime)
            is OrienteeringResultsAction.ApproveResults -> approveResults()
            is OrienteeringResultsAction.OpenSplits -> openSplits(action.participantId)
            is OrienteeringResultsAction.ExportCsv -> exportCsv()
            is OrienteeringResultsAction.ExportPdf -> exportPdf()
            is OrienteeringResultsAction.PublishHtml -> publishHtml()
            is OrienteeringResultsAction.DismissPublishedUrl -> updateState { copy(publishedHtmlUrl = null) }
        }
    }

    private fun openSplits(participantId: String) {
        val compId = competitionId ?: return
        viewModelScope.launch {
            navigation.navigate(CenterNavigation.ParticipantSplitsRoute(participantId, compId))
        }
    }

    /**
     * Обновляет результат участника.
     *
     * @param participantWithResult Данные участника и его текущий результат.
     * @param startTime Новое время старта.
     * @param finishTime Новое время финиша.
     */
    private fun updateParticipantResult(participantWithResult: ParticipantWithResult, startTime: Long?, finishTime: Long?) {
        val currentResult = participantWithResult.result ?: return
        val updatedResult = currentResult.copy(
            startTime = startTime,
            finishTime = finishTime,
            isEdited = true,
            totalTime = if (startTime != null && finishTime != null) (finishTime - startTime) / 1000 else null
        )
        viewModelScope.launch(Dispatchers.IO) {
            orienteeringCompetitionInteractor.updateParticipantResult(updatedResult)
            loadResults()
        }
    }

    /**
     * Утверждает результаты для текущего соревнования.
     */
    private fun approveResults() {
        competitionId?.let { id ->
            viewModelScope.launch(Dispatchers.IO) {
                orienteeringCompetitionInteractor.approveResults(id).onSuccess {
                    loadResults()
                }
            }
        }
    }

    private fun exportCsv() {
        val groups = stateValue.groupsWithParticipantsAndResults
        if (groups.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val title = stateValue.competitionTitle
            val csv = buildCsvContent(title, groups)
            val fileName = buildSafeFileName(title)
            _exportCsvEvent.emit(fileName to csv)
        }
    }

    private fun exportPdf() {
        val groups = stateValue.groupsWithParticipantsAndResults
        if (groups.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val title = stateValue.competitionTitle
            val bytes = buildPdfBytes(title, groups)
            val fileName = buildSafeFileName(title).replace(".csv", ".pdf")
            _exportPdfEvent.emit(fileName to bytes)
        }
    }

    private fun buildSafeFileName(title: String): String {
        val safe = title.replace(Regex("[^а-яА-ЯёЁa-zA-Z0-9_\\- ]"), "").trim().take(40)
        return if (safe.isNotEmpty()) "results_$safe.csv" else "results.csv"
    }

    private fun buildCsvContent(title: String, groups: List<GroupWithParticipantsAndResults>): String {
        val sb = StringBuilder("﻿") // UTF-8 BOM для корректного открытия в Excel
        if (title.isNotEmpty()) {
            sb.appendLine("Соревнование: $title")
            sb.appendLine()
        }
        groups.forEach { group ->
            val cpOrder = group.participants
                .mapNotNull { it.result?.splits }
                .maxByOrNull { it.size }
                ?.map { it.controlPoint }
                ?: emptyList()

            sb.appendLine("Группа: ${group.group.title}")
            val headerSuffix = if (cpOrder.isNotEmpty()) ";${buildCpHeaders(cpOrder)}" else ""
            sb.appendLine("Место;Фамилия;Имя;Команда;Старт;Финиш;Результат;Статус$headerSuffix")

            group.participants.forEach { pw ->
                val rank = pw.result?.rank?.toString() ?: ""
                val start = DateTimeFormat.transformLongToTime(pw.result?.startTime) ?: ""
                val finish = DateTimeFormat.transformLongToTime(pw.result?.finishTime) ?: ""
                val total = pw.result?.totalTime?.toRaceTime() ?: ""
                val status = when (pw.result?.status) {
                    ResultStatus.FINISHED -> "Финиш"
                    ResultStatus.DSQ      -> "Снят"
                    ResultStatus.DNS      -> "Не стартовал"
                    ResultStatus.DNF      -> "Сошёл"
                    else                  -> ""
                }
                val row = "$rank;${pw.participant.lastName};${pw.participant.firstName};${pw.participant.commandName};$start;$finish;$total;$status"
                if (cpOrder.isNotEmpty()) {
                    val splitValues = buildSplitValues(pw.result?.splits ?: emptyList(), pw.result?.startTime ?: 0L, cpOrder)
                    sb.appendLine("$row;$splitValues")
                } else {
                    sb.appendLine(row)
                }
            }
            sb.appendLine()
        }
        return sb.toString()
    }

    /** Строит заголовки колонок сплитов с суффиксом [n] для дублирующихся КП. */
    private fun buildCpHeaders(cpOrder: List<Int>): String {
        val cpCounts = mutableMapOf<Int, Int>()
        val hasDuplicates = cpOrder.groupingBy { it }.eachCount().any { it.value > 1 }
        return cpOrder.joinToString(";") { cp ->
            val count = cpCounts.merge(cp, 1, Int::plus)!!
            val tag = if (hasDuplicates && cpOrder.count { it == cp } > 1) "[$count]" else ""
            "КП$cp${tag}_δ;КП$cp${tag}_Σ"
        }
    }

    /**
     * Строит строку значений сплитов для одного участника.
     * Использует позиционный индекс (splits[i] ↔ cpOrder[i]), что корректно обрабатывает
     * дублирующиеся номера КП в дистанции.
     */
    private fun buildSplitValues(splits: List<com.competra.domain.models.orienteering.SplitTime>, startTs: Long, cpOrder: List<Int>): String {
        return buildString {
            cpOrder.forEachIndexed { i, _ ->
                if (i > 0) append(";")
                if (i < splits.size) {
                    val splitTs = splits[i].timestamp
                    val prevTs = if (i == 0) startTs else splits[i - 1].timestamp
                    val delta = ((splitTs - prevTs) / 1000L).toRaceTime()
                    val cumul = ((splitTs - startTs) / 1000L).toRaceTime()
                    append("$delta;$cumul")
                } else {
                    append(";") // два пустых столбца для пропущенного КП
                }
            }
        }
    }

    private fun buildPdfBytes(title: String, groups: List<GroupWithParticipantsAndResults>): ByteArray {
        val document = PdfDocument()
        val pW = 595; val pH = 842
        val margin = 36f
        val lineH = 16f
        val smallLineH = 14f

        val paintTitle  = Paint().apply { textSize = 16f; isFakeBoldText = true; isAntiAlias = true }
        val paintGroup  = Paint().apply { textSize = 13f; isFakeBoldText = true; isAntiAlias = true }
        val paintHeader = Paint().apply { textSize = 9f;  isFakeBoldText = true; isAntiAlias = true }
        val paintBody   = Paint().apply { textSize = 9f;  isAntiAlias = true }
        val paintSplit  = Paint().apply { textSize = 8f;  color = android.graphics.Color.DKGRAY; isAntiAlias = true }

        var pageNum = 1
        var page = document.startPage(PdfDocument.PageInfo.Builder(pW, pH, pageNum).create())
        var canvas = page.canvas
        var y = margin + paintTitle.textSize

        fun ensureSpace(needed: Float) {
            if (y + needed > pH - margin) {
                document.finishPage(page)
                page = document.startPage(PdfDocument.PageInfo.Builder(pW, pH, ++pageNum).create())
                canvas = page.canvas
                y = margin + paintBody.textSize
            }
        }

        if (title.isNotEmpty()) {
            canvas.drawText(title, margin, y, paintTitle)
            y += lineH * 1.5f
        }

        groups.forEach { group ->
            val cpOrder = group.participants
                .mapNotNull { it.result?.splits }.maxByOrNull { it.size }
                ?.map { it.controlPoint } ?: emptyList()

            ensureSpace(lineH * 3)
            canvas.drawText("Группа: ${group.group.title}", margin, y, paintGroup)
            y += lineH
            canvas.drawText(
                "М   Фамилия Имя              Команда           Старт      Финиш      Результат   Статус",
                margin, y, paintHeader
            )
            y += smallLineH

            group.participants.forEach { pw ->
                ensureSpace(lineH + if (cpOrder.isNotEmpty()) smallLineH else 0f)

                val rank   = (pw.result?.rank?.toString() ?: "-").padEnd(3)
                val name   = "${pw.participant.lastName} ${pw.participant.firstName}".take(20).padEnd(20)
                val team   = pw.participant.commandName.take(18).padEnd(18)
                val start  = (DateTimeFormat.transformLongToTime(pw.result?.startTime) ?: "").padEnd(10)
                val finish = (DateTimeFormat.transformLongToTime(pw.result?.finishTime) ?: "").padEnd(10)
                val total  = (pw.result?.totalTime?.toRaceTime() ?: "").padEnd(10)
                val status = when (pw.result?.status) {
                    ResultStatus.FINISHED -> "Финиш"
                    ResultStatus.DSQ      -> "Снят"
                    ResultStatus.DNS      -> "Не стартовал"
                    ResultStatus.DNF      -> "Сошёл"
                    else                  -> "-"
                }
                canvas.drawText("$rank $name $team $start $finish $total $status", margin, y, paintBody)
                y += lineH

                if (cpOrder.isNotEmpty()) {
                    val splits = pw.result?.splits ?: emptyList()
                    val startTs = pw.result?.startTime ?: 0L
                    val splitsLine = buildString {
                        cpOrder.forEachIndexed { i, cp ->
                            if (i > 0) append("  ")
                            if (i < splits.size) {
                                val splitTs = splits[i].timestamp
                                val prevTs  = if (i == 0) startTs else splits[i - 1].timestamp
                                val delta = ((splitTs - prevTs) / 1000L).toRaceTime()
                                val cumul = ((splitTs - startTs) / 1000L).toRaceTime()
                                append("КП$cp: $delta ($cumul)")
                            } else {
                                append("КП$cp: —")
                            }
                        }
                    }
                    canvas.drawText(splitsLine.take(110), margin + 12f, y, paintSplit)
                    y += smallLineH
                }
            }
            y += lineH * 0.5f
        }

        document.finishPage(page)
        val out = ByteArrayOutputStream()
        document.writeTo(out)
        document.close()
        return out.toByteArray()
    }

    private fun publishHtml() {
        val groups = stateValue.groupsWithParticipantsAndResults
        if (groups.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            updateState { copy(isPublishingHtml = true) }
            val title = stateValue.competitionTitle
            val html = buildHtmlContent(title, groups)
            val bytes = html.toByteArray(Charsets.UTF_8)
            uploadRepository.uploadFile(bytes, "results.html", "competition_results")
                .onSuccess { url ->
                    val compId = competitionId ?: return@onSuccess
                    val comp = orienteeringCompetitionInteractor.getCompetition(compId) ?: return@onSuccess
                    val updated = comp.copy(
                        competition = comp.competition.copy(
                            resultsUrl = url,
                            isSynced = false,
                        )
                    )
                    orienteeringCompetitionInteractor.updateCompetition(updated)
                    updateState { copy(isPublishingHtml = false, publishedHtmlUrl = url) }
                }
                .onFailure {
                    updateState { copy(isPublishingHtml = false) }
                }
        }
    }

    private fun buildHtmlContent(title: String, groups: List<GroupWithParticipantsAndResults>): String {
        val sb = StringBuilder()
        sb.append(
            """<!DOCTYPE html>
<meta content='text/html'; charset='utf-8' http-equiv='Content-Type'>
<style>
body {font-family: 'Arial Narrow'; font-size: 10pt;}
table.rezult {font-family: 'Arial Narrow'; font-size: 10pt; border: 1px solid #999999; border-collapse: collapse; background: #DDDDDD;text-align: center;}
table.rezult td{ margin:0; padding: 2px 5px;  border: 1px solid #999999; border-collapse: collapse; background: #FFFFFF;}
.yl, tr.yl td {background: #FFFFBB;}
.cr {text-align:left}
.rezult th { font-family: 'Arial Narrow';font-style: italic; font-size: 10pt; color: #FFFFFF;padding: 2px 3px; border: 1px solid #999999; border-collapse: collapse; background: #112255;}
H1  {font-family: 'Arial Narrow';font-size: 14pt;font-weight: bold;color: #112255;text-align: left;}
H2  {font-family: 'Arial Narrow';font-size: 12pt;font-weight: bold;color: #112255;text-align: left;}
H3  {font-family: 'Arial Narrow';font-size: 10pt;font-weight: bold;color: #112255;text-align: left;}
span.group  {font-family: 'Arial Narrow';font-size: 12pt;font-weight: bold;}
</style>
"""
        )

        val titleText = if (title.isNotEmpty()) "$title. Промежуточные времена" else "Промежуточные времена"
        sb.appendLine("<h1>$titleText</h1>")
        sb.appendLine("<h3>Данный протокол не является официальным документом</h3>")
        sb.appendLine("<a name=\"uppoint\">  </a>")
        sb.appendLine("<br><br>")

        val groupNavigation = groups.joinToString("    ") { g ->
            "<a href=\"#${g.group.title}\">${g.group.title}</a>"
        }

        groups.forEachIndexed { groupIndex, group ->
            val cpOrder = group.participants
                .mapNotNull { it.result?.splits }.maxByOrNull { it.size }
                ?.map { it.controlPoint } ?: emptyList()

            val cumulRanks: List<Map<String, Int>> = cpOrder.indices.map { i ->
                group.participants
                    .mapNotNull { pw ->
                        val splits = pw.result?.splits ?: return@mapNotNull null
                        val startTs = pw.result?.startTime ?: return@mapNotNull null
                        if (i < splits.size) pw.participant.id to (splits[i].timestamp - startTs) else null
                    }
                    .sortedBy { it.second }
                    .mapIndexed { rank, (id, _) -> id to (rank + 1) }
                    .toMap()
            }

            val deltaRanks: List<Map<String, Int>> = cpOrder.indices.map { i ->
                if (i == 0) emptyMap()
                else group.participants
                    .mapNotNull { pw ->
                        val splits = pw.result?.splits ?: return@mapNotNull null
                        if (i < splits.size) {
                            val prevTs = splits[i - 1].timestamp
                            pw.participant.id to (splits[i].timestamp - prevTs)
                        } else null
                    }
                    .sortedBy { it.second }
                    .mapIndexed { rank, (id, _) -> id to (rank + 1) }
                    .toMap()
            }

            val leaderTotalTime = group.participants
                .filter { it.result?.status == ResultStatus.FINISHED }
                .minByOrNull { it.result?.totalTime ?: Long.MAX_VALUE }
                ?.result?.totalTime

            sb.appendLine("<a name=\"${group.group.title}\"></a>")
            if (groupIndex == 0) {
                sb.appendLine("<span class='group'>$groupNavigation</span><br>")
            }
            sb.appendLine("<h2>${group.group.title}</h2>")

            sb.append("<table class='rezult'>\n<tr>")
            sb.append("<th>№ п/п </th><th>Ст.№ </th><th>Фамилия, Имя </th><th>Команда </th>")
            sb.append("<th>Результат </th><th>Место </th><th>Отставание </th>")
            cpOrder.forEachIndexed { i, cp -> sb.append("<th>#${i + 1} ($cp) </th>") }
            sb.appendLine("</tr>")

            group.participants.forEachIndexed { rowIdx, pw ->
                val rowAttr = if (rowIdx % 2 == 0) "style='background: #FFFFFF;'" else "class='yl'"
                sb.append("<tr $rowAttr>")

                sb.append("<td><nobr>${rowIdx + 1}</td>")
                sb.append("<td><nobr>${pw.participant.startNumber}</td>")
                sb.append("<td class='cr'><nobr>${pw.participant.lastName} ${pw.participant.firstName}</td>")
                sb.append("<td class='cr'><nobr>${pw.participant.commandName}</td>")

                val totalTime = pw.result?.totalTime?.toRaceTime() ?: ""
                val statusText = when (pw.result?.status) {
                    ResultStatus.FINISHED -> totalTime
                    ResultStatus.DSQ      -> "снят"
                    ResultStatus.DNS      -> "н/с"
                    ResultStatus.DNF      -> "не финишировал"
                    else                  -> ""
                }
                sb.append("<td><nobr>$statusText</td>")
                sb.append("<td><nobr>${pw.result?.rank?.toString() ?: ""}</td>")

                val gap = if (pw.result?.status == ResultStatus.FINISHED && leaderTotalTime != null) {
                    val diff = (pw.result?.totalTime ?: 0L) - leaderTotalTime
                    if (diff <= 0L) "" else "+${formatGap(diff)}"
                } else ""
                sb.append("<td><nobr>$gap</td>")

                val splits = pw.result?.splits ?: emptyList()
                val startTs = pw.result?.startTime ?: 0L

                cpOrder.indices.forEach { i ->
                    if (i >= splits.size) {
                        sb.append("<td><nobr></td>")
                        return@forEach
                    }
                    val splitTs = splits[i].timestamp
                    val cumulSec = (splitTs - startTs) / 1000L
                    val cumulStr = cumulSec.toRaceTime()
                    val cumulRank = cumulRanks[i][pw.participant.id] ?: 0
                    val cumulCell = if (cumulRank == 1) "<b><nobr>$cumulStr($cumulRank)</b>" else "<nobr>$cumulStr($cumulRank)"

                    if (i == 0) {
                        sb.append("<td>$cumulCell<br></td>")
                    } else {
                        val prevTs = splits[i - 1].timestamp
                        val deltaSec = (splitTs - prevTs) / 1000L
                        val deltaStr = deltaSec.toRaceTime()
                        val deltaRank = deltaRanks[i][pw.participant.id] ?: 0
                        val deltaCell = if (deltaRank == 1) "<b><nobr>$deltaStr($deltaRank)</b>" else "<nobr>$deltaStr($deltaRank)"
                        sb.append("<td>$cumulCell<br>$deltaCell</td>")
                    }
                }

                sb.appendLine("</tr>")
            }
            sb.appendLine("</table>")
            sb.appendLine("<br>")
        }

        return sb.toString()
    }

    private fun formatGap(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) "$h:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
        else "$m:${s.toString().padStart(2, '0')}"
    }

    /**
     * Действия на экране результатов.
     */
    sealed class OrienteeringResultsAction : BaseAction {
        data class UpdateResult(
            val participantWithResult: ParticipantWithResult,
            val startTime: Long?,
            val finishTime: Long?
        ) : OrienteeringResultsAction()

        object ApproveResults : OrienteeringResultsAction()

        data class OpenSplits(val participantId: String) : OrienteeringResultsAction()

        data object ExportCsv : OrienteeringResultsAction()

        data object ExportPdf : OrienteeringResultsAction()

        data object PublishHtml : OrienteeringResultsAction()

        data object DismissPublishedUrl : OrienteeringResultsAction()
    }
}
