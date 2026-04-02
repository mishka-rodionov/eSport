package com.rodionov.center.data

import com.rodionov.domain.models.orienteering.OrienteeringCompetition

sealed class CenterEffects {
    data object OpenKindOfSports: CenterEffects()
    data object OpenOrienteeringCreator: CenterEffects()
    data class OpenOrienteeringEditor(val competitionId: Long): CenterEffects()
    data class OpenOrienteeringEventControl(val competitionId: Long): CenterEffects()
    data class ShowDeleteCompetitionDialog(val competition: OrienteeringCompetition): CenterEffects()
    data object HideDeleteCompetitionDialog: CenterEffects()
    data class DeleteCompetition(val competition: OrienteeringCompetition): CenterEffects()
}