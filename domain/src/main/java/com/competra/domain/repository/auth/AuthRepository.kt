package com.competra.domain.repository.auth

import com.competra.domain.models.auth.Token
import com.competra.domain.models.user.User

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
     * @param privacyAccepted Согласие на обработку персональных данных.
     */
    suspend fun register(firstName: String, lastName: String, bdate: Long, email: String, privacyAccepted: Boolean): Result<Any>

    /**
     * Безвозвратное удаление аккаунта текущего пользователя на сервере.
     */
    suspend fun deleteAccount(): Result<Any>

}
