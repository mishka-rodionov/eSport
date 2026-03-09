package com.rodionov.center.data

sealed class CenterEffects {
    data object OpenKindOfSports: CenterEffects()
    data object OpenOrienteeringCreator: CenterEffects()
    data class OpenOrienteeringEditor(val competitionId: Long): CenterEffects()
    data class OpenOrienteeringEventControl(val competitionId: Long): CenterEffects()
}