package com.rodionov.center.data.read_card

import com.rodionov.domain.models.ResultStatus
import com.rodionov.domain.models.orienteering.SplitTime

data class CheckResult(
    val status: ResultStatus,
    val message: String? = null,
    val validSplits: List<SplitTime> = emptyList()
)
