package com.competra.profile.presentation.push_preferences

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.domain.models.push.PushCategory
import com.competra.domain.repository.push.PushPreferencesRepository
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel экрана настроек push-уведомлений: три независимых тумблера категорий,
 * хранятся локально в [PushPreferencesRepository] (см. там же — почему не синхронизируются с backend).
 */
class PushPreferencesViewModel(
    private val pushPreferencesRepository: PushPreferencesRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<PushPreferencesState>(PushPreferencesState()) {

    init {
        loadPreferences()
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is PushPreferencesAction.ToggleResultsPublished -> toggle(PushCategory.RESULTS_PUBLISHED, action.enabled) { copy(resultsPublished = action.enabled) }
            is PushPreferencesAction.ToggleCompetitionStart -> toggle(PushCategory.COMPETITION_START, action.enabled) { copy(competitionStart = action.enabled) }
            is PushPreferencesAction.ToggleDayBeforeReminder -> toggle(PushCategory.DAY_BEFORE_REMINDER, action.enabled) { copy(dayBeforeReminder = action.enabled) }
        }
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            val resultsPublished = pushPreferencesRepository.isEnabled(PushCategory.RESULTS_PUBLISHED)
            val competitionStart = pushPreferencesRepository.isEnabled(PushCategory.COMPETITION_START)
            val dayBeforeReminder = pushPreferencesRepository.isEnabled(PushCategory.DAY_BEFORE_REMINDER)
            updateState {
                copy(
                    resultsPublished = resultsPublished,
                    competitionStart = competitionStart,
                    dayBeforeReminder = dayBeforeReminder,
                    isLoading = false,
                )
            }
        }
    }

    private fun toggle(category: PushCategory, enabled: Boolean, newState: suspend PushPreferencesState.() -> PushPreferencesState) {
        analytics.trackEvent(
            AnalyticsEvent.ProfilePushCategoryToggled(
                category = AnalyticsEvent.PushCategory.valueOf(category.name),
                enabled = enabled,
            )
        )
        updateState(newState)
        viewModelScope.launch {
            pushPreferencesRepository.setEnabled(category, enabled)
        }
    }
}

/** Действия на экране настроек push-уведомлений. */
sealed interface PushPreferencesAction : BaseAction {
    data class ToggleResultsPublished(val enabled: Boolean) : PushPreferencesAction
    data class ToggleCompetitionStart(val enabled: Boolean) : PushPreferencesAction
    data class ToggleDayBeforeReminder(val enabled: Boolean) : PushPreferencesAction
}
