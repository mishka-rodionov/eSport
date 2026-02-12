package com.rodionov.domain.models.orienteering

import com.rodionov.domain.models.ResultStatus

data class OrienteeringResult(
    val id: Long = 0,
    val competitionId: Long,
    val groupId: Long,
    val participantId: Long,
    val startTime: Long? = null,
    val finishTime: Long? = null,
    val totalTime: Long? = null, // в секундах
    val rank: Int? = null,
    val status: ResultStatus, // FINISHED, DSQ, DNS, DNF
    val penaltyTime: Long = 0, // Штрафное время
    val splits: List<SplitTime>? = null // Можно хранить как JSON или отдельной таблицей
)
