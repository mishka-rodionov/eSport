package com.rodionov.center.presentation.results

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.center.data.results.OrienteeringCompetitionResultsState
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.models.orienteering.ParticipantWithResult
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants
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
                updateState { copy(
                    groupsWithParticipantsAndResults =  results
                ) }
            }
        }
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is OrienteeringResultsAction.UpdateResult -> updateParticipantResult(action.participantWithResult, action.startTime, action.finishTime)
            is OrienteeringResultsAction.ApproveResults -> approveResults()
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
        /**
         * Обновление результата конкретного участника.
         */
        data class UpdateResult(
            val participantWithResult: ParticipantWithResult,
            val startTime: Long?,
            val finishTime: Long?
        ) : OrienteeringResultsAction()

        /**
         * Утверждение всех результатов соревнования.
         */
        object ApproveResults : OrienteeringResultsAction()
    }
}
