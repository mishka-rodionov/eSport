package com.rodionov.local.repository.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.security.crypto.MasterKey
import com.rodionov.domain.models.auth.Token
import com.rodionov.domain.repository.auth.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class TokenRepositoryImpl(
    private val context: Context
): TokenRepository {

    private val secretKey = KeyStoreManager.getOrCreateSecretKey()

    private val dataStore: DataStore<Token> = DataStoreFactory.create(
        serializer = TokenSerializer(secretKey),
        produceFile = { context.dataStoreFile("secure_tokens.pb") }
    )

    val accessTokenFlow: Flow<String?> = dataStore.data.map { it.accessToken }
    val refreshTokenFlow: Flow<String?> = dataStore.data.map { it.refreshToken }

    override suspend fun saveTokens(access: String, refresh: String) {
        dataStore.updateData {
            it.copy(accessToken = access, refreshToken = refresh)
        }
    }

    override suspend fun getAccessToken(): String? = accessTokenFlow.firstOrNull()
    override suspend fun getRefreshToken(): String? = refreshTokenFlow.firstOrNull()

    override suspend fun clear() {
        dataStore.updateData { Token(null, null) }
    }
}