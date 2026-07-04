package com.competra.domain.repository

import com.competra.domain.models.onboarding.OnboardingSource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Синглтон-репозиторий для запроса ручного повторного показа онбординга
 * из feature-модулей в [app] через SharedFlow.
 */
class OnboardingRequestRepository {
    private val _events = MutableSharedFlow<OnboardingSource>(extraBufferCapacity = 1)
    val events: SharedFlow<OnboardingSource> = _events.asSharedFlow()

    suspend fun emit(source: OnboardingSource) = _events.emit(source)
}
