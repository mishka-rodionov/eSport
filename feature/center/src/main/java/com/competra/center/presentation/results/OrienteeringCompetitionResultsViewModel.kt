package com.competra.center.presentation.results

import androidx.lifecycle.viewModelScope
import com.competra.center.data.interactors.OrienteeringCompetitionInteractor
import com.competra.center.data.results.OrienteeringCompetitionResultsState
import com.competra.data.navigation.CenterNavigation
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.getArguments
import com.competra.domain.models.ResultStatus
import com.competra.domain.models.orienteering.OrienteeringResult
import com.competra.domain.models.orienteering.ParticipantWithResult
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import com.competra.utils.constants.EventsConstants
import kotlinx.coroutines.Dispatchers
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

    init {
        loadResults()
    }

    /**
     * Загружает результаты соревнований и обновляет состояние.
     */
    private fun loadResults() {
        competitionId?.let {
            viewModelScope.launch(Dispatchers.IO) {
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
                updateState { copy(groupsWithParticipantsAndResults = sortedResults, isApproved = isApproved) }
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
    }
}
