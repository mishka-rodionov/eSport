package com.competra.analytics

/**
 * Контракт продуктовой аналитики приложения.
 *
 * Реализация прячет конкретного провайдера (AppMetrica), чтобы feature-модули
 * могли слать события через единый словарь без прямой зависимости от SDK.
 */
interface AnalyticsTracker {

    /** Зарегистрировать факт открытия экрана. */
    fun trackScreen(screen: AnalyticsScreen)

    /** Зарегистрировать продуктовое событие. */
    fun trackEvent(event: AnalyticsEvent)

    /**
     * Привязать события сессии к пользователю.
     * Передать `null` при logout, чтобы очистить связку.
     */
    fun setUserId(userId: String?)
}
