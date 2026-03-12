package com.rodionov.remote.response.orienteering

import com.google.gson.annotations.SerializedName
import com.rodionov.domain.models.orienteering.PunchingSystem
import com.rodionov.domain.models.orienteering.StartTimeMode
import com.rodionov.remote.response.competition.CompetitionResponse

/**
 * Ответ сервера с информацией о соревновании по ориентированию.
 * 
 * @property competitionId Уникальный идентификатор
 * @property competition Базовая информация
 * @property direction Направление
 * @property punchingSystem Система отметки
 * @property startTimeMode Режим начала соревнования
 */
data class OrienteeringCompetitionResponse(
    @SerializedName("competitionId")
    val competitionId: Long,

    @SerializedName("competition")
    val competition: CompetitionResponse,

    @SerializedName("direction")
    val direction: String, // "FORWARD", "BY_CHOICE", etc.

    @SerializedName("punchingSystem")
    val punchingSystem: PunchingSystem,

    @SerializedName("startTimeMode")
    val startTimeMode: StartTimeMode
)
