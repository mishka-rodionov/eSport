package com.competra.analytics.di

import com.competra.analytics.AnalyticsTracker
import com.competra.analytics.AppMetricaAnalyticsTracker
import org.koin.dsl.module

/**
 * Koin-модуль ядра аналитики.
 *
 * Регистрирует только реализацию [AnalyticsTracker]. Активация AppMetrica SDK
 * выполняется на уровне `:app` (в `CompetraApp.onCreate()`), потому что
 * требует доступ к `BuildConfig` приложения и должна выполняться один раз
 * на процесс.
 */
val analyticsCoreModule = module {
    single<AnalyticsTracker> { AppMetricaAnalyticsTracker() }
}
