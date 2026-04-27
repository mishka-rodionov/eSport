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

    private fun loadCompetitionData() {
        val id = competitionId ?: return
        viewModelScope.launch {
            loadingRepository.emit(true)
            val remoteId = orienteeringCompetitionInteractor.getCompetition(id)
                ?.competition?.remoteId
            if (remoteId != null) {
                orienteeringCompetitionInteractor.fetchAndSyncFromServer(remoteId, id)
                orienteeringCompetitionInteractor.fetchAndSyncParticipantsFromServer(remoteId, id)
            }

            orienteeringCompetitionInteractor.getCompetitionWithDetails(id).onSuccess { details ->
                val allChipsDistributed = details.groupsWithParticipants.all { group ->
                    group.participants.all { it.isChipGiven }
                }
                val competition = details.competition
                val isRunning = competition.competition.status == CompetitionStatus.IN_PROGRESS
                val allParticipantsFinished = isRunning &&
                        orienteeringCompetitionInteractor.areAllParticipantsFinished(id)
                applyCompetitionState(
                    title = competition.competition.title,
                    competition = competition,
                    groups = details.groupsWithParticipants.map { it.group },
                    allChipsDistributed = allChipsDistributed,
                    allParticipantsFinished = allParticipantsFinished
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
     * Использует status == IN_PROGRESS как источник истины для isCompetitionRunning,
     * поскольку startTime может быть сброшен при синхронизации с сервером.
     * Таймер обратного отсчёта возобновляется, если startTime ещё в будущем.
     */
    private fun applyCompetitionState(
        title: String,
        competition: com.rodionov.domain.models.orienteering.OrienteeringCompetition,
        groups: List<com.rodionov.domain.models.ParticipantGroup> = emptyList(),
        allChipsDistributed: Boolean = true,
        allParticipantsFinished: Boolean = false
    ) {
        val startTime = competition.startTime
        val now = System.currentTimeMillis()
        val isFinished = competition.competition.status == CompetitionStatus.FINISHED
        val isRunning = competition.competition.status == CompetitionStatus.IN_PROGRESS
        val isCountingDown = isRunning && startTime != null && startTime > now
        val remainingMillis = if (isCountingDown) startTime - now else 0L

        updateState {
            copy(
                competitionTitle = title,
                participantGroups = groups,
                competition = competition,
                isCompetitionRunning = isRunning,
                isTimerRunning = isCountingDown,
                countdownMillis = remainingMillis,
                countdownTimerInput = competition.countdownTimer?.toString() ?: "",
                allChipsDistributed = allChipsDistributed,
                allParticipantsFinished = allParticipantsFinished,
                isFinished = isFinished
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

            OrientEventControlAction.ShowStartConfirmDialog ->
                updateState { copy(isShowStartConfirmDialog = true) }

            OrientEventControlAction.HideStartConfirmDialog ->
                updateState { copy(isShowStartConfirmDialog = false) }

            OrientEventControlAction.StartCompetition -> {
                updateState { copy(isShowStartConfirmDialog = false) }
                handleStartCompetition()
            }

            OrientEventControlAction.ShowStopConfirmDialog ->
                updateState { copy(isShowStopConfirmDialog = true) }

            OrientEventControlAction.HideStopConfirmDialog ->
                updateState { copy(isShowStopConfirmDialog = false) }

            OrientEventControlAction.StopCompetition -> {
                updateState { copy(isShowStopConfirmDialog = false) }
                handleStopCompetition()
            }

            OrientEventControlAction.CancelCountdown -> handleCancelCountdown()

            is OrientEventControlAction.UpdateCountdownTimerInput -> updateState {
                copy(countdownTimerInput = action.value)
            }

            OrientEventControlAction.Reload -> loadCompetitionData()
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
     * Меняет статус на FINISHED, останавливает таймер, выставляет DNF незафинишировавшим
     * и синхронизирует с сервером.
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
                competitionId?.let { id ->
                    orienteeringCompetitionInteractor.markNonFinishedAsDNF(id)
                }
            }
            serviceController.stop()
            loadingRepository.emit(false)
        }
        updateState { copy(isCompetitionRunning = false, isTimerRunning = false, isFinished = true) }
    }

    /**
     * Отменяет предстартовый таймер и возвращает соревнование в статус DRAFT.
     */
    private fun handleCancelCountdown() {
        timerJob?.cancel()
        val competition = stateValue.competition ?: return
        val reverted = competition.copy(
            startTime = null,
            competition = competition.competition.copy(status = CompetitionStatus.DRAFT)
        )
        viewModelScope.launch {
            loadingRepository.emit(true)
            orienteeringCompetitionInteractor.updateCompetition(reverted, null)
            orienteeringCompetitionInteractor.publishCompetitionToServer(reverted)
                .onFailure { handleFailure(it) }
            serviceController.stop()
            loadingRepository.emit(false)
        }
        updateState {
            copy(
                competition = reverted,
                countdownMillis = 0L,
                isTimerRunning = false,
                isCompetitionRunning = false
            )
        }
    }

    /**
     * Запускает таймер обратного отсчета.
     * Для режима USER_SET по истечении таймера пересчитывает фактическое время старта
     * и стартовые времена всех участников.
     */
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (stateValue.countdownMillis > 0) {
                delay(1000)
                updateState { copy(countdownMillis = countdownMillis - 1000) }
            }
            updateState { copy(isTimerRunning = false) }
            onCountdownFinished()
        }
    }

    /**
     * Вызывается после обнуления таймера.
     * Для режима USER_SET фиксирует фактическое время старта и пересчитывает
     * стартовые времена участников с учётом стартового интервала.
     */
    private fun onCountdownFinished() {
        val competition = stateValue.competition ?: return
        if (competition.startTimeMode != StartTimeMode.USER_SET) return
        val compId = competitionId ?: return

        viewModelScope.launch {
            val actualStartTime = System.currentTimeMillis()
            val updatedCompetition = competition.copy(startTime = actualStartTime)
            orienteeringCompetitionInteractor.updateCompetition(updatedCompetition, null)
            orienteeringCompetitionInteractor.publishCompetitionToServer(updatedCompetition)
                .onFailure { handleFailure(it) }

            val intervalMs = (competition.startIntervalSeconds ?: 60) * 1000L
            val participants = orienteeringCompetitionInteractor.getParticipants(compId).getOrNull()
            if (!participants.isNullOrEmpty()) {
                val updated = participants.map { p ->
                    val number = p.startNumber.toIntOrNull() ?: return@map p
                    p.copy(startTime = actualStartTime + (number - 1) * intervalMs)
                }
                orienteeringCompetitionInteractor.updateParticipants(updated)
            }

            updateState { copy(competition = updatedCompetition) }
        }
    }

    private fun handleFailure(throwable: Throwable) {
        viewModelScope.launch {
            val code = (throwable as? NetworkException)?.code
            networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
        }
    }
}
