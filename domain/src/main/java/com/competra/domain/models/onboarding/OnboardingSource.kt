package com.competra.domain.models.onboarding

/**
 * Источник запроса показа онбординга.
 */
enum class OnboardingSource {
    /** Первый запуск приложения, сразу после сплэша. */
    FIRST_LAUNCH,

    /** Ручной повторный показ из настроек профиля. */
    SETTINGS
}
