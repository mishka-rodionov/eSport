package com.competra.center.presentation.group_splits

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.center.data.group_splits.GroupSplitsTableState
import com.competra.center.data.interactors.OrienteeringCompetitionInteractor
import com.competra.center.data.results.buildSplitsTable
import com.competra.center.data.results.sortedForResults
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupSplitsTableViewModel(
    private val interactor: OrienteeringCompetitionInteractor,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<GroupSplitsTableState>(GroupSplitsTableState()) {

    override fun onAction(action: BaseAction) {}

    fun load(groupId: Long, competitionId: String) {
        analytics.trackEvent(AnalyticsEvent.GroupSplitsTableOpened(groupId, competitionId))
        viewModelScope.launch(Dispatchers.IO) {
            val group = interactor.getResultsByGroups(competitionId).getOrNull()
                ?.firstOrNull { it.group.groupId == groupId }

            if (group == null) {
                updateState { copy(isLoading = false) }
                return@launch
            }

            val sortedGroup = group.copy(participants = group.participants.sortedForResults())
            val table = buildSplitsTable(sortedGroup)
            updateState {
                copy(groupTitle = sortedGroup.group.title, table = table, isLoading = false)
            }
        }
    }
}
