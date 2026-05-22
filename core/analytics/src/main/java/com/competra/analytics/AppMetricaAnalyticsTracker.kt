package com.competra.analytics

import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.profile.Attribute
import io.appmetrica.analytics.profile.UserProfile

/**
 * Реализация [AnalyticsTracker] поверх Яндекс AppMetrica.
 *
 * Активация SDK выполняется в `:app` (после старта Koin), здесь только
 * репортинг событий — то есть класс безопасен к созданию даже если
 * `AppMetrica.activate(...)` не был вызван (события просто не уйдут).
 */
class AppMetricaAnalyticsTracker : AnalyticsTracker {

    override fun trackScreen(screen: AnalyticsScreen) {
        AppMetrica.reportEvent(
            EVENT_SCREEN_VIEW,
            mapOf<String, Any?>(PARAM_SCREEN to screen.screenName),
        )
    }

    override fun trackEvent(event: AnalyticsEvent) {
        val params = event.params
        if (params.isEmpty()) {
            AppMetrica.reportEvent(event.eventName)
        } else {
            AppMetrica.reportEvent(event.eventName, params)
        }
    }

    override fun setUserId(userId: String?) {
        AppMetrica.setUserProfileID(userId)
        if (userId != null) {
            val profile = UserProfile.newBuilder()
                .apply(Attribute.customString(ATTR_USER_ID).withValue(userId))
                .build()
            AppMetrica.reportUserProfile(profile)
        }
    }

    private companion object {
        const val EVENT_SCREEN_VIEW = "screen_view"
        const val PARAM_SCREEN = "screen"
        const val ATTR_USER_ID = "user_id"
    }
}
