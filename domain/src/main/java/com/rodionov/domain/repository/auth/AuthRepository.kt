package com.rodionov.domain.repository.auth

import com.rodionov.domain.models.auth.Token
import com.rodionov.domain.models.user.User

/**
 * Репозиторий для аутентификации и регистрации пользователей.
 */
interface AuthRepository {

    /**
     * Запрос на вход пользователя по email.
     */
    suspend fun login(email: String): Result<Any>

    /**
     * Авторизация пользователя по email и коду подтверждения.
     */
    suspend fun authorize(email: String, code: String): Result<Pair<User, Token>>

    /**
     * Регистрация нового пользователя.
     * 
     * @param bdate Дата рождения в формате Long (timestamp).
     */
    suspend fun register(firstName: String, lastName: String, bdate: Long, email: String): Result<Any>

}
