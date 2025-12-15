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
        when(action) {
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
                val sortedParticipants = drawParticipants(participants = participants, punchingSystem = punchingSystem)
                interactor.updateParticipants(sortedParticipants)
                updateState { copy(participants = sortedParticipants) }
            }
        }
    }

    fun drawParticipants(
        participants: List<OrienteeringParticipant>,
        punchingSystem: PunchingSystem?
    ): List<OrienteeringParticipant> {

        if (participants.isEmpty()) return emptyList()

        // Группируем по groupId
        val groups = participants
            .groupBy { it.groupId }
            .toMutableMap()

        val result = mutableListOf<OrienteeringParticipant>()
        var lastGroupId: Long? = null

        while (groups.isNotEmpty()) {

            // выбираем группы, отличные от предыдущей
            val availableGroups = groups
                .filterKeys { it != lastGroupId }
                .ifEmpty { groups } // если выбора нет — берем любую

            // случайная группа
            val selectedGroupId = availableGroups.keys.random()
            val groupList = groups[selectedGroupId]!!.toMutableList()

            // случайный участник из группы
            val participant = groupList.random()
            result.add(participant)

            // обновляем группу
            groupList.remove(participant)
            if (groupList.isEmpty()) {
                groups.remove(selectedGroupId)
            } else {
                groups[selectedGroupId] = groupList
            }

            lastGroupId = selectedGroupId
        }

        // присваиваем стартовые номера
        return result
            .mapIndexed { index, participant ->
                val number = (index + 1).toString()
                participant.copy(startNumber = number)
                if (punchingSystem == PunchingSystem.SPORTIDUINO) {
                    participant.copy(chipNumber = number)
                }
                participant
            }
            .sortedBy { it.startNumber.toInt() }
    }

}