package com.competra.local.repository.onboarding

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.competra.domain.repository.onboarding.OnboardingRepository
import kotlinx.coroutines.flow.first

private val Context.onboardingDataStore by preferencesDataStore(name = "onboarding_prefs")

class OnboardingRepositoryImpl(
    private val context: Context
) : OnboardingRepository {

    private val onboardingSeenKey = booleanPreferencesKey("onboarding_seen")

    override suspend fun isSeen(): Boolean =
        context.onboardingDataStore.data.first()[onboardingSeenKey] ?: false

    override suspend fun markSeen() {
        context.onboardingDataStore.edit { prefs ->
            prefs[onboardingSeenKey] = true
        }
    }
}
