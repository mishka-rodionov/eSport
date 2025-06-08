package com.rodionov.domain.models

data class ParticipantGroup(
    val title: String,
    val distance: Double,
    val countOfControls: Int,
    val sequenceOfControl: List<Int>,
    val maxTimeInMinute: Int
)