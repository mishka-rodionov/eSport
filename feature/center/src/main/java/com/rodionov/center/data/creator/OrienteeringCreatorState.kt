package com.rodionov.center.data.creator

import com.rodionov.domain.models.Competition
import com.rodionov.domain.models.Coordinates
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.ParticipantGroup
import java.time.LocalDate

data class OrienteeringCreatorState(
    val title: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: String = "12:00",
    val address: String = "",
    val description: String = "",
    val participantGroups: List<ParticipantGroup> = emptyList(),
    val errors: OrienteeringCreatorErrors = OrienteeringCreatorErrors(),
    val isShowGroupCreateDialog: Boolean = false,
    val editGroupIndex: Int = -1,
    val competitionDirection: OrienteeringDirection? = null
) {
    fun constructOrienteeringCompetition(): OrienteeringCompetition {
        return OrienteeringCompetition(
            competitionId = -1L,
            competition = Competition(
                title = title,
                date = date,
                kindOfSport = KindOfSport.Orienteering,
                description = description,
                address = address,
                coordinates = Coordinates(0.0, 0.0)
            ),
            direction = competitionDirection ?: OrienteeringDirection.FORWARD
        )
    }
}