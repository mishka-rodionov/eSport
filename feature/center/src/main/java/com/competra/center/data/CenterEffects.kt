package com.competra.center.data

import com.competra.domain.models.orienteering.OrienteeringCompetition

sealed class CenterEffects {
    data object OpenKindOfSports: CenterEffects()
    data object OpenOrienteeringCreator: CenterEffects()
    data class OpenOrienteeringEditor(val competitionId: String): CenterEffects()
    data class OpenOrienteeringEventControl(val competitionId: String): CenterEffects()
    data class ShowDeleteCompetitionDialog(val competition: OrienteeringCompetition): CenterEffects()
    data object HideDeleteCompetitionDialog: CenterEffects()
    data class DeleteCompetition(val competition: OrienteeringCompetition): CenterEffects()
}