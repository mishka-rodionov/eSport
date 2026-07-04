package com.competra.onboarding.di

import com.competra.domain.models.onboarding.OnboardingSource
import com.competra.onboarding.presentation.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingModule = module {
    viewModel { (source: OnboardingSource) -> OnboardingViewModel(source, get(), get()) }
}
