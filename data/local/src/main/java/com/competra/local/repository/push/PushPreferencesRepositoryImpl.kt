package com.competra.local.repository.push

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.competra.domain.models.push.PushCategory
import com.competra.domain.repository.push.PushPreferencesRepository
import kotlinx.coroutines.flow.first

private val Context.pushPrefsDataStore by preferencesDataStore(name = "push_prefs")

class PushPreferencesRepositoryImpl(
    private val context: Context
) : PushPreferencesRepository {

    private fun keyFor(category: PushCategory) = booleanPreferencesKey("push_enabled_${category.name}")

    override suspend fun isEnabled(category: PushCategory): Boolean =
        context.pushPrefsDataStore.data.first()[keyFor(category)] ?: true

    override suspend fun setEnabled(category: PushCategory, enabled: Boolean) {
        context.pushPrefsDataStore.edit { prefs ->
            prefs[keyFor(category)] = enabled
        }
    }
}
