package com.competra.domain.repository.push

import com.competra.domain.models.push.PushCategory

/**
 * Хранит пользовательские предпочтения по категориям push-уведомлений. Локальный, не синхронизируется
 * с backend — единственный потребитель настроек сейчас сам Android-клиент (см. [PushCategory]).
 * По умолчанию все категории включены (opt-out, не opt-in).
 */
interface PushPreferencesRepository {

    suspend fun isEnabled(category: PushCategory): Boolean

    suspend fun setEnabled(category: PushCategory, enabled: Boolean)
}
