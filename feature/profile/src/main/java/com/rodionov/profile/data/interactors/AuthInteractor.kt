package com.rodionov.profile.data.interactors

import com.rodionov.domain.repository.auth.AuthRepository
import com.rodionov.domain.repository.auth.TokenRepository
import com.rodionov.domain.repository.user.UserRepository

class AuthInteractor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
) {

    suspend fun authorize(email: String, code: String): Boolean {
        val (user, token) = authRepository.authorize(email, code).getOrNull() ?: return false
        val accessToken = token.accessToken
        val refreshToken = token.refreshToken
        if (accessToken != null && refreshToken != null) {
            tokenRepository.saveTokens(accessToken, refreshToken)
        }
        userRepository.saveUser(user)
    }

}