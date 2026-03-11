package com.rodionov.center.presentation.draw

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.draw.DrawAction
import com.rodionov.center.data.draw.DrawState
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.getArguments
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.PunchingSystem
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.constants.EventsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Вьюмодель жеребьевки участников соревнований.
 * */
class DrawViewModel(
    private val interactor: OrienteeringCompetitionInteractor,
    private val navigation: Navigation
) : BaseViewModel<DrawState>(DrawState()) {

    val competitionId: Long? = navigation.getArguments<Long>(EventsConstants.EVENT_ID.name)
    var competition: OrienteeringCompetition? = null

    init {
        competitionId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                competition = interactor.getCompetition(it)
            }
        }
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            DrawAction.StartDrawOperation -> startDrawOperation()
        }
    }

    private fun startDrawOperation() {
        competitionId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val participants =
                    interactor.getParticipants(competitionId = competitionId).getOrNull()
                        ?: return@launch
                val punchingSystem = competition?.punchingSystem
                val sortedParticipants =
                    drawParticipants(
                        participants = participants.shuffled(),
                        punchingSystem = punchingSystem
                    )
                interactor.updateParticipants(sortedParticipants)
                updateState { copy(participants = sortedParticipants) }
            }
        }
    }

    /**
     * Функция жеребьевки участников соревнований
     * */
    private fun drawParticipants(
        participants: List<OrienteeringParticipant>,
        punchingSystem: PunchingSystem?
    ): List<OrienteeringParticipant> {

        if (participants.isEmpty()) return emptyList()

        // Группируем по groupId для последующего чередования
        val groups = participants
            .groupBy { it.groupId }
            .mapValues { it.value.toMutableList() }
            .toMutableMap()

        val mixedResult = mutableListOf<OrienteeringParticipant>()
        var lastGroupId: Long? = null

        // Жеребьевка: перемешиваем участников из разных групп для исключения старта подряд участников одной группы
        while (groups.isNotEmpty()) {

            // выбираем группы, отличные от предыдущей (если возможно)
            val availableGroups = groups
                .filterKeys { it != lastGroupId }
                .ifEmpty { groups } // если выбора нет — берем любую оставшуюся

            // случайная группа
            val selectedGroupId = availableGroups.keys.random()
            val groupList = groups[selectedGroupId]!!

//            val groupList = if (tempList.any { it.startNumber.isEmpty() }) {
//                globalIndex += 1000
//                tempList.mapIndexed { index, participant ->
//                    participant.copy(startNumber = (index + globalIndex).toString())
//                }
//                    .toMutableList()
//            } else {
//                tempList.toMutableList()
//            }
            // случайный участник из группы
            val participant = groupList.random()
            mixedResult.add(participant)

            // обновляем группу
            groupList.remove(participant)
            if (groupList.isEmpty()) {
                groups.remove(selectedGroupId)
            }

            lastGroupId = selectedGroupId
        }

        // Присваиваем стартовые номера и адекватное время старта (с 1-й минуты)
        return mixedResult.mapIndexed { index, participant ->
            val number = (index + 1).toString()
            val time = (index + 1).toLong() // Время в минутах: 1, 2, 3...

            val freshParticipant = participant.copy(
                startNumber = number,
                startTime = time,
                chipNumber = if (punchingSystem == PunchingSystem.SPORTIDUINO) number else participant.chipNumber
            )
            freshParticipant
        }
    }

}
