package com.rodionov.domain.repository.auth

interface TokenRepository {

    suspend fun saveTokens(access: String, refresh: String)

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun clear()

}