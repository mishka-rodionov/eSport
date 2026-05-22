package com.competra.center.presentation.draw

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.center.data.draw.DrawAction
import com.competra.center.data.draw.DrawState
import com.competra.center.data.interactors.OrienteeringCompetitionInteractor
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.getArguments
import com.competra.domain.models.orienteering.OrienteeringParticipant
import com.competra.domain.models.orienteering.PunchingSystem
import com.competra.domain.repository.LoadingRepository
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import com.competra.utils.constants.EventsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Минимальный допустимый timestamp — 1 января 2000 года. */
private const val MIN_VALID_TIMESTAMP_MS = 946_684_800_000L

/**
 * Вьюмодель жеребьевки участников соревнований.
 */
class DrawViewModel(
    private val interactor: OrienteeringCompetitionInteractor,
    private val navigation: Navigation,
    private val loadingRepository: LoadingRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<DrawState>(DrawState()) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)

    override fun onAction(action: BaseAction) {
        when (action) {
            DrawAction.StartDrawOperation -> startDrawOperation()
            DrawAction.StartGroupDrawOperation -> startGroupDrawOperation()
        }
    }

    /**
     * Общая жеребьевка: участники из всех групп перемешиваются вместе с чередованием групп.
     * Стартовые номера и времена назначаются глобально — каждый следующий участник
     * стартует на `startIntervalSeconds` позже предыдущего.
     */
    private fun startDrawOperation() {
        val compId = competitionId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            loadingRepository.emit(true)
            try {
                val competition = interactor.getCompetition(compId) ?: return@launch
                val participants = interactor.getParticipants(competitionId = compId).getOrNull()
                    ?: return@launch
                val competitionStartTime = resolveStartTime(competition)
                val intervalMs = (competition.startIntervalSeconds?.toLong() ?: 60L) * 1000L
                val sortedParticipants = drawParticipants(
                    participants = participants.shuffled(),
                    punchingSystem = competition.punchingSystem,
                    competitionStartTime = competitionStartTime,
                    intervalMs = intervalMs
                )
                interactor.updateParticipants(sortedParticipants)
                interactor.syncParticipantsAfterDraw(sortedParticipants)
                interactor.setDrawConducted(compId)
                analytics.trackEvent(AnalyticsEvent.ParticipantDrawn(sortedParticipants.size))
                updateState { copy(participants = sortedParticipants) }
            } finally {
                loadingRepository.emit(false)
            }
        }
    }

    /**
     * Жеребьевка по группам: внутри каждой группы участники перемешиваются независимо.
     * Стартовое время отсчитывается от начала соревнования отдельно для каждой группы,
     * поэтому участники из разных групп могут стартовать в одно и то же время.
     * Стартовые номера уникальны глобально.
     */
    private fun startGroupDrawOperation() {
        val compId = competitionId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            loadingRepository.emit(true)
            try {
                val competition = interactor.getCompetition(compId) ?: return@launch
                val participants = interactor.getParticipants(competitionId = compId).getOrNull()
                    ?: return@launch
                val competitionStartTime = resolveStartTime(competition)
                val intervalMs = (competition.startIntervalSeconds?.toLong() ?: 60L) * 1000L
                val sortedParticipants = drawParticipantsByGroups(
                    participants = participants,
                    punchingSystem = competition.punchingSystem,
                    competitionStartTime = competitionStartTime,
                    intervalMs = intervalMs
                )
                interactor.updateParticipants(sortedParticipants)
                interactor.syncParticipantsAfterDraw(sortedParticipants)
                interactor.setDrawConducted(compId)
                analytics.trackEvent(AnalyticsEvent.ParticipantDrawn(sortedParticipants.size))
                updateState { copy(participants = sortedParticipants) }
            } finally {
                loadingRepository.emit(false)
            }
        }
    }

    /**
     * Определяет базовое время старта соревнования для жеребьевки.
     *
     * Приоритет:
     * 1. [OrienteeringCompetition.startTime] — фактическое время старта, если задано и валидно.
     * 2. [Competition.startDate] — дата соревнования, если валидна.
     * 3. Текущее время — крайний запасной вариант.
     */
    private fun resolveStartTime(competition: com.competra.domain.models.orienteering.OrienteeringCompetition): Long {
        return competition.startTime?.takeIf { it >= MIN_VALID_TIMESTAMP_MS }
            ?: competition.competition.startDate.takeIf { it >= MIN_VALID_TIMESTAMP_MS }
            ?: System.currentTimeMillis()
    }

    /**
     * Жеребьевка по группам: внутри каждой группы участники перемешиваются независимо.
     * Стартовое время каждого участника — [competitionStartTime] + позиция_в_группе * intervalMs.
     * Участники из разных групп с одинаковой позицией получают одинаковое стартовое время.
     * Стартовые номера назначаются глобально (уникальны по всем группам).
     */
    private fun drawParticipantsByGroups(
        participants: List<OrienteeringParticipant>,
        punchingSystem: PunchingSystem?,
        competitionStartTime: Long,
        intervalMs: Long
    ): List<OrienteeringParticipant> {
        if (participants.isEmpty()) return emptyList()

        val result = mutableListOf<OrienteeringParticipant>()
        var globalNumber = 1

        participants
            .groupBy { it.groupId }
            .values
            .forEach { groupParticipants ->
                groupParticipants.shuffled().forEachIndexed { indexInGroup, participant ->
                    val number = globalNumber.toString()
                    val startTime = competitionStartTime + (indexInGroup + 1) * intervalMs
                    result.add(
                        participant.copy(
                            startNumber = number,
                            startTime = startTime,
                            chipNumber = if (punchingSystem == PunchingSystem.SPORTIDUINO) number else participant.chipNumber
                        )
                    )
                    globalNumber++
                }
            }

        return result
    }

    /**
     * Общая жеребьевка: участники перемешиваются с чередованием групп,
     * чтобы подряд не стартовали участники одной группы.
     * Стартовое время каждого участника — [competitionStartTime] + глобальная_позиция * intervalMs.
     */
    private fun drawParticipants(
        participants: List<OrienteeringParticipant>,
        punchingSystem: PunchingSystem?,
        competitionStartTime: Long,
        intervalMs: Long
    ): List<OrienteeringParticipant> {
        if (participants.isEmpty()) return emptyList()

        // Группируем по groupId для чередования
        val groups = participants
            .groupBy { it.groupId }
            .mapValues { it.value.toMutableList() }
            .toMutableMap()

        val mixedResult = mutableListOf<OrienteeringParticipant>()
        var lastGroupId: Long? = null

        // Перемешиваем с чередованием групп
        while (groups.isNotEmpty()) {
            val availableGroups = groups
                .filterKeys { it != lastGroupId }
                .ifEmpty { groups }

            val selectedGroupId = availableGroups.keys.random()
            val groupList = groups[selectedGroupId]!!

            val participant = groupList.random()
            mixedResult.add(participant)

            groupList.remove(participant)
            if (groupList.isEmpty()) {
                groups.remove(selectedGroupId)
            }

            lastGroupId = selectedGroupId
        }

        // Присваиваем стартовые номера и времена по глобальной позиции
        return mixedResult.mapIndexed { index, participant ->
            val number = (index + 1).toString()
            val startTime = competitionStartTime + (index + 1) * intervalMs
            participant.copy(
                startNumber = number,
                startTime = startTime,
                chipNumber = if (punchingSystem == PunchingSystem.SPORTIDUINO) number else participant.chipNumber
            )
        }
    }
}
