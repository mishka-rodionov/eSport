package com.competra.onboarding.presentation

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.domain.models.onboarding.OnboardingSource
import com.competra.domain.repository.onboarding.OnboardingRepository
import com.competra.onboarding.data.OnboardingAction
import com.competra.onboarding.data.OnboardingState
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel онбординга. Создаётся заново на каждый показ (см. `viewModelKey`
 * в OnboardingScreen), поэтому не переиспользует состояние между показами.
 */
class OnboardingViewModel(
    private val source: OnboardingSource,
    private val onboardingRepository: OnboardingRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<OnboardingState>(OnboardingState()) {

    init {
        analytics.trackEvent(AnalyticsEvent.OnboardingStarted(source.toAnalyticsSource()))
    }

    override fun onAction(action: BaseAction) {
        if (action !is OnboardingAction) return
        when (action) {
            is OnboardingAction.PageChanged -> updateState { copy(currentPage = action.index) }
            OnboardingAction.NextClicked -> updateState {
                copy(currentPage = (currentPage + 1).coerceAtMost(pageCount - 1))
            }
            OnboardingAction.SkipClicked -> skip()
            OnboardingAction.StartClicked -> complete()
        }
    }

    private fun skip() {
        analytics.trackEvent(AnalyticsEvent.OnboardingSkipped(stateValue.currentPage))
        finish()
    }

    private fun complete() {
        analytics.trackEvent(AnalyticsEvent.OnboardingCompleted)
        finish()
    }

    private fun finish() {
        viewModelScope.launch {
            onboardingRepository.markSeen()
            updateState { copy(isFinished = true) }
        }
    }

    private fun OnboardingSource.toAnalyticsSource(): AnalyticsEvent.OnboardingSource = when (this) {
        OnboardingSource.FIRST_LAUNCH -> AnalyticsEvent.OnboardingSource.FIRST_LAUNCH
        OnboardingSource.SETTINGS -> AnalyticsEvent.OnboardingSource.SETTINGS
    }
}
