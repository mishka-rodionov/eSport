package com.competra.center.presentation.results

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
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import com.competra.utils.DateTimeFormat
import com.competra.utils.constants.EventsConstants
import com.competra.utils.orienteering.toRaceTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана результатов соревнований по ориентированию.
 * Управляет состоянием экрана, загрузкой данных и обработкой действий пользователя.
 *
 * @param orienteeringCompetitionInteractor Интерактор для работы с данными соревнований.
 * @param navigation Навигация для перехода между экранами.
 */
class OrienteeringCompetitionResultsViewModel(
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor,
    private val navigation: Navigation
): BaseViewModel<OrienteeringCompetitionResultsState>(OrienteeringCompetitionResultsState()) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)

    private val _exportCsvEvent = MutableSharedFlow<Pair<String, String>>()
    val exportCsvEvent: SharedFlow<Pair<String, String>> = _exportCsvEvent

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
            is OrienteeringResultsAction.ExportResults -> exportResults()
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

    private fun exportResults() {
        val groups = stateValue.groupsWithParticipantsAndResults
        if (groups.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val title = stateValue.competitionTitle
            val csv = buildCsvContent(title, groups)
            val fileName = buildSafeFileName(title)
            _exportCsvEvent.emit(fileName to csv)
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
            val splitHeaders = cpOrder.joinToString(";") { "КП$it" }
            val headerSuffix = if (cpOrder.isNotEmpty()) ";$splitHeaders" else ""
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
                val splitsMap = pw.result?.splits?.associateBy { it.controlPoint } ?: emptyMap()
                val startTs = pw.result?.startTime ?: 0L
                val splitValues = cpOrder.joinToString(";") { cp ->
                    splitsMap[cp]?.let { ((it.timestamp - startTs) / 1000L).toRaceTime() } ?: ""
                }
                val row = "$rank;${pw.participant.lastName};${pw.participant.firstName};${pw.participant.commandName};$start;$finish;$total;$status"
                sb.appendLine(if (cpOrder.isNotEmpty()) "$row;$splitValues" else row)
            }
            sb.appendLine()
        }
        return sb.toString()
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

        data object ExportResults : OrienteeringResultsAction()
    }
}
