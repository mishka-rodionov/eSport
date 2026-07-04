package com.competra.eventdetails.presentation.race_graph

import com.competra.domain.models.orienteering.RaceGraphData
import com.competra.ui.BaseState

/** Участников больше этого числа по умолчанию скрываем с графика — иначе он нечитаем. */
const val EVENT_RACE_GRAPH_DEFAULT_VISIBLE_COUNT = 10

data class EventRaceGraphState(
    val groupTitle: String = "",
    val data: RaceGraphData? = null,
    val visibleParticipantIds: Set<String> = emptySet(),
    val highlightedParticipantId: String? = null,
    val isLoading: Boolean = true,
) : BaseState
