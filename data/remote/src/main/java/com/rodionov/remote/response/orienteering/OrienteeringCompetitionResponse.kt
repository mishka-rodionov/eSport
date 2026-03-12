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
 * @property countdownTimer Время отсчета перед стартом (в минутах)
 * @property startTime Фактическое время начала соревнования (timestamp)
 */
data class OrienteeringCompetitionResponse(
    @SerializedName("competitionId")
    val competitionId: Long,

    @SerializedName("competition")
    val competition: CompetitionResponse,

    @SerializedName("direction")
    val direction: String,

    @SerializedName("punchingSystem")
    val punchingSystem: PunchingSystem,

    @SerializedName("startTimeMode")
    val startTimeMode: StartTimeMode,

    @SerializedName("countdownTimer")
    val countdownTimer: Int? = null,

    @SerializedName("startTime")
    val startTime: Long? = null
)
