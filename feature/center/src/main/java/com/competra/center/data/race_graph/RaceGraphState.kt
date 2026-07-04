package com.competra.center.data.race_graph

import com.competra.domain.models.orienteering.RaceGraphData
import com.competra.ui.BaseState

/** Участников больше этого числа по умолчанию скрываем с графика — иначе он нечитаем. */
const val RACE_GRAPH_DEFAULT_VISIBLE_COUNT = 10

data class RaceGraphState(
    val groupTitle: String = "",
    val data: RaceGraphData? = null,
    val visibleParticipantIds: Set<String> = emptySet(),
    val highlightedParticipantId: String? = null,
    val isLoading: Boolean = true,
) : BaseState
