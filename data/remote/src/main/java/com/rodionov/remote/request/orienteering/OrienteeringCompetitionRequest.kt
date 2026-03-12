package com.rodionov.remote.request.orienteering

import com.google.gson.annotations.SerializedName
import com.rodionov.remote.request.competition.CompetitionRequest

/**
 * Запрос для создания/обновления соревнования по ориентированию на сервере.
 * 
 * @property competitionId Уникальный идентификатор
 * @property competition Базовая информация
 * @property direction Направление
 * @property punchingSystem Система отметки
 * @property startTimeMode Режим начала соревнования
 * @property countdownTimer Время отсчета перед стартом (в минутах)
 */
data class OrienteeringCompetitionRequest(
    @SerializedName("competitionId")
    val competitionId: Long,

    @SerializedName("competition")
    val competition: CompetitionRequest,

    @SerializedName("direction")
    val direction: String,

    @SerializedName("punchingSystem")
    val punchingSystem: String,

    @SerializedName("startTimeMode")
    val startTimeMode: String,

    @SerializedName("countdownTimer")
    val countdownTimer: Int? = null
)
