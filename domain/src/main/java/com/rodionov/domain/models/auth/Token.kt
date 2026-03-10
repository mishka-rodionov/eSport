package com.rodionov.domain.models.auth

/**
 * Представляет собой учетные данные аутентификации, содержащие токены доступа и обновления.
 *
 * @property accessToken Токен доступа для аутентификации запросов к API.
 * @property refreshToken Токен обновления, используемый для получения новой пары токенов после истечения срока действия [accessToken].
 */
data class Token(
    val accessToken: String?,
    val refreshToken: String?
)