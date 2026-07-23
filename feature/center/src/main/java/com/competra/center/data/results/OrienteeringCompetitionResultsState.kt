package com.competra.center.data.results

import com.competra.domain.models.orienteering.GroupWithParticipantsAndResults
import com.competra.domain.models.orienteering.ResultsStatus
import com.competra.ui.BaseState

data class OrienteeringCompetitionResultsState(
    val groupsWithParticipantsAndResults: List<GroupWithParticipantsAndResults> = emptyList(),
    val isApproved: Boolean = false,
    /** Соревнование уже завершено — кнопка "Утвердить результаты" больше не показывается. */
    val isCompetitionFinished: Boolean = false,
    val competitionTitle: String = "",
    val isPublishingHtml: Boolean = false,
    val publishedHtmlUrl: String? = null,
    val importDiff: ImportResultsDiff? = null,
    val isImporting: Boolean = false,
    val importError: String? = null,
    /** Статус публикации результатов участникам (push-уведомление шлётся при переходе в PRELIMINARY/OFFICIAL). */
    val resultsStatus: ResultsStatus = ResultsStatus.NOT_PUBLISHED,
    val isPublishingResults: Boolean = false,
    val isShowPublishResultsConfirm: Boolean = false,
): BaseState