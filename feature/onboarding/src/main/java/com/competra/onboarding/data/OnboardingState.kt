package com.competra.onboarding.data

import com.competra.ui.BaseState

data class OnboardingState(
    val currentPage: Int = 0,
    val pageCount: Int = 4,
    val isFinished: Boolean = false,
) : BaseState
