package com.competra.eventdetails.presentation.score_graph

import com.competra.domain.models.orienteering.ScoreGraphData
import com.competra.ui.BaseState

/** Участников больше этого числа по умолчанию скрываем с графика — иначе он нечитаем. */
const val EVENT_SCORE_GRAPH_DEFAULT_VISIBLE_COUNT = 10

data class EventScoreGraphState(
    val groupTitle: String = "",
    val data: ScoreGraphData? = null,
    val visibleParticipantIds: Set<String> = emptySet(),
    val highlightedParticipantId: String? = null,
    val isLoading: Boolean = true,
) : BaseState
