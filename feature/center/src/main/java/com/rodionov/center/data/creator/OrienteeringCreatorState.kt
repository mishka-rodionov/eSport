package com.rodionov.center.data.creator

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.PunchingSystem
import com.rodionov.ui.BaseState
import java.time.LocalDate

data class OrienteeringCreatorState(
    val competitionId: Long? = null,
    val title: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: String = "12:00",
    val address: String = "",
    val description: String = "",
    val participantGroups: List<ParticipantGroup> = emptyList(),
    val errors: OrienteeringCreatorErrors = OrienteeringCreatorErrors(),
    val isShowGroupCreateDialog: Boolean = false,
    val punchingSystem: PunchingSystem = PunchingSystem.SPORTIDUINO,
    val editGroupIndex: Int = -1,
    val competitionDirection: OrienteeringDirection? = null
) : BaseState {
    fun constructOrienteeringCompetition(userId: String): OrienteeringCompetition {
        return OrienteeringCompetition(
            competitionId = competitionId ?: (-9999..-1000).random().toLong(),
            competition = Competition(
                title = title,
                date = date,
                kindOfSport = KindOfSport.Orienteering,
                description = description,
                address = address,
                mainOrganizer = userId,
                coordinates = Coordinates(0.0, 0.0)
            ),
            direction = competitionDirection ?: OrienteeringDirection.FORWARD,
            punchingSystem = punchingSystem
        )
    }
}