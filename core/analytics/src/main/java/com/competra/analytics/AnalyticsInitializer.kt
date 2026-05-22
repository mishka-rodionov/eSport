package com.competra.analytics

import android.content.Context
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig

/**
 * Активирует Яндекс AppMetrica, если передан непустой [apiKey].
 *
 * Инициализация инкапсулирована в `:core:analytics`, чтобы `:app`
 * не зависел напрямую от AppMetrica SDK — модуль знает про SDK только сам.
 *
 * Пустой ключ означает, что приложение собирается без секретов (CI, сборка
 * без `local.properties`) — SDK тогда не активируется, и вызовы
 * [AnalyticsTracker] становятся no-op.
 *
 * Crash reporting у AppMetrica выключен намеренно: крэши собирает
 * Firebase Crashlytics, дублировать не нужно.
 */
fun initAppMetrica(context: Context, apiKey: String) {
    if (apiKey.isBlank()) return

    val config = AppMetricaConfig.newConfigBuilder(apiKey)
        .withLogs()
        .withSessionTimeout(SESSION_TIMEOUT_SECONDS)
        .withCrashReporting(false)
        .withAnrMonitoring(true)
        .build()
    AppMetrica.activate(context, config)
    val app = context.applicationContext as? android.app.Application
    if (app != null) AppMetrica.enableActivityAutoTracking(app)
}

private const val SESSION_TIMEOUT_SECONDS = 30
