package com.competra.center.data.read_card

import com.competra.domain.models.ResultStatus
import com.competra.domain.models.orienteering.SplitTime

data class CheckResult(
    val status: ResultStatus,
    val message: String? = null,
    val validSplits: List<SplitTime> = emptyList(),
    val totalScore: Int? = null,
    val scorePenalty: Int = 0
)
