package com.competra.domain.repository.onboarding

/**
 * Хранит глобальный флаг «онбординг просмотрен». Не привязан к пользователю —
 * онбординг показывается независимо от авторизации.
 */
interface OnboardingRepository {

    suspend fun isSeen(): Boolean

    suspend fun markSeen()
}
