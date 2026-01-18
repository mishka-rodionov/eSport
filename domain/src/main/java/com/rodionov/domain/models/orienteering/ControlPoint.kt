package com.rodionov.domain.models.orienteering

data class ControlPoint(
    val number: Int,
    val role: ControlPointRole = ControlPointRole.ORDINARY,
    val score: Int = 0
)
