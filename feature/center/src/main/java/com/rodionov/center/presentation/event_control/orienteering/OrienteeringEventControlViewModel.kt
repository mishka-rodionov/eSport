package com.rodionov.center.presentation.event_control.orienteering

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.event_control.OrientEventControlAction
import com.rodionov.center.data.event_control.OrienteeringEventControlState
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.exception.NetworkException
import com.rodionov.domain.models.NetworkErrorEvent
import com.rodionov.domain.models.orienteering.CompetitionStatus
import com.rodionov.domain.models.orienteering.StartTimeMode
import com.rodionov.domain.repository.LoadingRepository
import com.rodionov.domain.repository.NetworkErrorRepository
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
    private val serviceController: CompetitionServiceController,
    private val networkErrorRepository: NetworkErrorRepository,
    private val loadingRepository: LoadingRepository
) : BaseViewModel<OrienteeringEventControlState>(OrienteeringEventControlState()) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)
    private var timerJob: Job? = null

    init {
        loadCompetitionData()
    }

    private fun loadCompetitionData() {
        val id = competitionId ?: return
        viewModelScope.launch {
            loadingRepository.emit(true)
            // Сначала синхронизируем с сервером, если соревнование опубликовано
            val remoteId = orienteeringCompetitionInteractor.getCompetition(id)
                ?.competition?.remoteId
            if (remoteId != null) {
                orienteeringCompetitionInteractor.fetchAndSyncFromServer(remoteId, id)
                orienteeringCompetitionInteractor.fetchAndSyncParticipantsFromServer(remoteId, id)
            }

            // Загружаем актуальные данные из локальной БД (уже синхронизированной)
            orienteeringCompetitionInteractor.getCompetitionWithDetails(id).onSuccess { details ->
                val competition = details.competition
                applyCompetitionState(
                    competition.competition.title,
                    competition,
                    groups = details.groupsWithParticipants.map { it.group }
                )
            }.onFailure {
                orienteeringCompetitionInteractor.getCompetition(id)?.let { competition ->
                    applyCompetitionState(competition.competition.title, competition)
                }
                handleFailure(it)
            }
            loadingRepository.emit(false)
        }
    }

    /**
     * Применяет загруженное соревнование к стейту.
     * Если соревнование уже запущено (startTime != null), восстанавливает флаги
     * и при необходимости возобновляет таймер обратного отсчёта.
     */
    private fun applyCompetitionState(
        title: String,
        competition: com.rodionov.domain.models.orienteering.OrienteeringCompetition,
        groups: List<com.rodionov.domain.models.ParticipantGroup> = emptyList()
    ) {
        val startTime = competition.startTime
        val now = System.currentTimeMillis()
        val isRunning = startTime != null
        val isCountingDown = startTime != null && startTime > now
        val remainingMillis = if (isCountingDown) startTime!! - now else 0L

        updateState {
            copy(
                competitionTitle = title,
                participantGroups = groups,
                competition = competition,
                isCompetitionRunning = isRunning,
                isTimerRunning = isCountingDown,
                countdownMillis = remainingMillis,
                countdownTimerInput = competition.countdownTimer?.toString() ?: ""
            )
        }

        if (isCountingDown) startTimer()
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

            is OrientEventControlAction.UpdateCountdownTimerInput -> updateState {
                copy(countdownTimerInput = action.value)
            }
        }
    }

    /**
     * Обрабатывает нажатие на кнопку "Старт".
     * Устанавливает время старта, меняет статус на IN_PROGRESS и синхронизирует с сервером.
     */
    private fun handleStartCompetition() {
        val competition = stateValue.competition ?: return
        val countdownMinutes = stateValue.countdownTimerInput.toIntOrNull()
            ?: competition.countdownTimer
            ?: 0
        val startTime = System.currentTimeMillis() + (countdownMinutes * 60 * 1000L)

        viewModelScope.launch {
            loadingRepository.emit(true)
            val updatedCompetition = competition.copy(
                startTime = startTime,
                countdownTimer = countdownMinutes,
                competition = competition.competition.copy(
                    status = CompetitionStatus.IN_PROGRESS
                )
            )
            orienteeringCompetitionInteractor.updateCompetition(
                orienteeringCompetition = updatedCompetition,
                participantGroups = null
            )
            orienteeringCompetitionInteractor.publishCompetitionToServer(updatedCompetition)
                .onFailure { handleFailure(it) }
            updateState {
                copy(
                    competition = updatedCompetition,
                    countdownMillis = countdownMinutes * 60 * 1000L,
                    isTimerRunning = true,
                    isCompetitionRunning = true
                )
            }
            startTimer()
            loadingRepository.emit(false)
            competitionId?.let { id -> serviceController.start(id, startTime) }
        }
    }

    /**
     * Обрабатывает завершение соревнования.
     * Меняет статус на FINISHED, останавливает таймер и синхронизирует с сервером.
     */
    private fun handleStopCompetition() {
        timerJob?.cancel()
        val competition = stateValue.competition
        viewModelScope.launch {
            loadingRepository.emit(true)
            if (competition != null) {
                val finished = competition.copy(
                    competition = competition.competition.copy(status = CompetitionStatus.FINISHED)
                )
                orienteeringCompetitionInteractor.updateCompetition(finished, null)
                orienteeringCompetitionInteractor.publishCompetitionToServer(finished)
                    .onFailure { handleFailure(it) }
            }
            serviceController.stop()
            loadingRepository.emit(false)
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

    private fun handleFailure(throwable: Throwable) {
        viewModelScope.launch {
            val code = (throwable as? NetworkException)?.code
            networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
        }
    }
}
