package com.competra.center.data.results

import com.competra.domain.models.orienteering.GroupWithParticipantsAndResults
import com.competra.ui.BaseState

data class OrienteeringCompetitionResultsState(
    val groupsWithParticipantsAndResults: List<GroupWithParticipantsAndResults> = emptyList(),
    val isApproved: Boolean = false,
    val competitionTitle: String = "",
    val isPublishingHtml: Boolean = false,
    val publishedHtmlUrl: String? = null,
    val importDiff: ImportResultsDiff? = null,
    val isImporting: Boolean = false,
    val importError: String? = null,
): BaseState