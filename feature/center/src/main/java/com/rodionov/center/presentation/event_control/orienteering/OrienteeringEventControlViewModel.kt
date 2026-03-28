package com.rodionov.center.presentation.event_control.orienteering

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.event_control.OrientEventControlAction
import com.rodionov.center.data.event_control.OrienteeringEventControlState
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.models.orienteering.StartTimeMode
import com.rodionov.ui.BaseAction
import com.rodionov.ui.CompetitionServiceController
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана управления соревнованием по ориентированию.
 *
 * @property navigation Навигация приложения.
 * @property orienteeringCompetitionInteractor Интерактор для работы с данными соревнований.
 */
class OrienteeringEventControlViewModel(
    private val navigation: Navigation,
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor,
    private val serviceController: CompetitionServiceController
) : BaseViewModel<OrienteeringEventControlState>(OrienteeringEventControlState()) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)
    private var timerJob: Job? = null

    init {
        loadCompetitionData()
    }

    private fun loadCompetitionData() {
        val id = competitionId ?: return
        viewModelScope.launch {
            orienteeringCompetitionInteractor.getCompetitionWithDetails(id).onSuccess { details ->
                updateState {
                    copy(
                        competitionTitle = details.competition.competition.title,
                        participantGroups = details.groupsWithParticipants.map { it.group },
                        competition = details.competition
                    )
                }
            }.onFailure {
                orienteeringCompetitionInteractor.getCompetition(id)?.let { competition ->
                    updateState {
                        copy(
                            competitionTitle = competition.competition.title,
                            competition = competition
                        )
                    }
                }
            }
        }
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            OrientEventControlAction.OpenOrientReadCard -> viewModelScope.launch {
                navigation.navigate(
                    destination = CenterNavigation.OrientReadCardRoute,
                    argument = navigation.createArguments(EventsConstants.EVENT_ID.name to competitionId)
                )
            }

            OrientEventControlAction.OpenParticipantLists -> {
                viewModelScope.launch {
                    navigation.navigate(
                        destination = CenterNavigation.ParticipantList,
                        argument = navigation.createArguments(EventsConstants.EVENT_ID.name to competitionId)
                    )
                }
            }

            OrientEventControlAction.OpenDrawParticipants -> {
                viewModelScope.launch {
                    navigation.navigate(
                        destination = CenterNavigation.DrawParticipants,
                        argument = navigation.createArguments(EventsConstants.EVENT_ID.name to competitionId)
                    )
                }
            }

            OrientEventControlAction.OpenResults -> {
                viewModelScope.launch {
                    navigation.navigate(
                        destination = CenterNavigation.ParticipantResults,
                        argument = navigation.createArguments(EventsConstants.EVENT_ID.name to competitionId)
                    )
                }
            }

            OrientEventControlAction.OpenGetOrienteeringChip -> {
                viewModelScope.launch {
                    competitionId?.let {
                        navigation.navigate(CenterNavigation.GetOrienteeringChipRoute(it))
                    }
                }
            }

            OrientEventControlAction.StartCompetition -> handleStartCompetition()
            OrientEventControlAction.StopCompetition -> handleStopCompetition()
        }
    }

    /**
     * Обрабатывает нажатие на кнопку "Старт".
     * Записывает время старта и запускает таймер отсчета.
     */
    private fun handleStartCompetition() {
        val competition = stateValue.competition ?: return
        val countdownMinutes = competition.countdownTimer ?: 0
        val startTime = System.currentTimeMillis() + (countdownMinutes * 60 * 1000L)

        viewModelScope.launch {
            val updatedCompetition = competition.copy(startTime = startTime)
            orienteeringCompetitionInteractor.updateCompetition(
                orienteeringCompetition = updatedCompetition,
                participantGroups = null
            )
            updateState {
                copy(
                    competition = updatedCompetition,
                    countdownMillis = countdownMinutes * 60 * 1000L,
                    isTimerRunning = true,
                    isCompetitionRunning = true
                )
            }
            startTimer()
            competitionId?.let { id -> serviceController.start(id, startTime) }
        }
    }

    /**
     * Обрабатывает завершение соревнования.
     * Останавливает таймер и отправляет команду остановки foreground-сервиса.
     */
    private fun handleStopCompetition() {
        timerJob?.cancel()
        viewModelScope.launch {
            serviceController.stop()
        }
        updateState { copy(isCompetitionRunning = false, isTimerRunning = false) }
    }

    /**
     * Запускает таймер обратного отсчета.
     */
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (stateValue.countdownMillis > 0) {
                delay(1000)
                updateState { copy(countdownMillis = countdownMillis - 1000) }
            }
            updateState { copy(isTimerRunning = false) }
        }
    }
}