package com.competra.onboarding.data

import com.competra.ui.BaseAction

sealed class OnboardingAction : BaseAction {

    data class PageChanged(val index: Int) : OnboardingAction()

    data object SkipClicked : OnboardingAction()

    data object NextClicked : OnboardingAction()

    data object StartClicked : OnboardingAction()
}
