package com.rodionov.center.presentation.draw

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.draw.DrawAction
import com.rodionov.center.data.draw.DrawState
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.PunchingSystem
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Интервал между стартами участников по умолчанию — 1 минута. */
private const val DRAW_INTERVAL_MS = 60_000L

/** Минимальный допустимый timestamp — 1 января 2000 года. */
private const val MIN_VALID_TIMESTAMP_MS = 946_684_800_000L

/**
 * Вьюмодель жеребьевки участников соревнований.
 */
class DrawViewModel(
    private val interactor: OrienteeringCompetitionInteractor,
    private val navigation: Navigation
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
     * стартует на [DRAW_INTERVAL_MS] позже предыдущего.
     */
    private fun startDrawOperation() {
        val compId = competitionId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val competition = interactor.getCompetition(compId) ?: return@launch
            val participants = interactor.getParticipants(competitionId = compId).getOrNull()
                ?: return@launch
            val competitionStartTime = resolveStartTime(competition)
            val sortedParticipants = drawParticipants(
                participants = participants.shuffled(),
                punchingSystem = competition.punchingSystem,
                competitionStartTime = competitionStartTime
            )
            interactor.updateParticipants(sortedParticipants)
            updateState { copy(participants = sortedParticipants) }
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
            val competition = interactor.getCompetition(compId) ?: return@launch
            val participants = interactor.getParticipants(competitionId = compId).getOrNull()
                ?: return@launch
            val competitionStartTime = resolveStartTime(competition)
            val sortedParticipants = drawParticipantsByGroups(
                participants = participants,
                punchingSystem = competition.punchingSystem,
                competitionStartTime = competitionStartTime
            )
            interactor.updateParticipants(sortedParticipants)
            updateState { copy(participants = sortedParticipants) }
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
    private fun resolveStartTime(competition: com.rodionov.domain.models.orienteering.OrienteeringCompetition): Long {
        return competition.startTime?.takeIf { it >= MIN_VALID_TIMESTAMP_MS }
            ?: competition.competition.startDate.takeIf { it >= MIN_VALID_TIMESTAMP_MS }
            ?: System.currentTimeMillis()
    }

    /**
     * Жеребьевка по группам: внутри каждой группы участники перемешиваются независимо.
     * Стартовое время каждого участника — [competitionStartTime] + позиция_в_группе * [DRAW_INTERVAL_MS].
     * Участники из разных групп с одинаковой позицией получают одинаковое стартовое время.
     * Стартовые номера назначаются глобально (уникальны по всем группам).
     */
    private fun drawParticipantsByGroups(
        participants: List<OrienteeringParticipant>,
        punchingSystem: PunchingSystem?,
        competitionStartTime: Long
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
                    val startTime = competitionStartTime + indexInGroup * DRAW_INTERVAL_MS
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
     * Стартовое время каждого участника — [competitionStartTime] + глобальная_позиция * [DRAW_INTERVAL_MS].
     */
    private fun drawParticipants(
        participants: List<OrienteeringParticipant>,
        punchingSystem: PunchingSystem?,
        competitionStartTime: Long
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
            val startTime = competitionStartTime + index * DRAW_INTERVAL_MS
            participant.copy(
                startNumber = number,
                startTime = startTime,
                chipNumber = if (punchingSystem == PunchingSystem.SPORTIDUINO) number else participant.chipNumber
            )
        }
    }
}
