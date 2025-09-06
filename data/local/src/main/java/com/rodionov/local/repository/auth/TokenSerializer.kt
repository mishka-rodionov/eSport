package com.rodionov.local.repository.auth

import androidx.datastore.core.Serializer
import com.rodionov.domain.models.auth.Token
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import javax.crypto.SecretKey

class TokenSerializer(private val secretKey: SecretKey) : Serializer<Token> {

    override val defaultValue: Token = Token(null, null)

    override suspend fun readFrom(input: InputStream): Token {
        return try {
            val bytes = input.readBytes()
            if (bytes.isEmpty()) return defaultValue

            val buffer = ByteBuffer.wrap(bytes)

            // читаем IV
            val ivLength = buffer.int
            val iv = ByteArray(ivLength)
            buffer.get(iv)

            // читаем зашифрованные данные
            val encryptedData = ByteArray(buffer.remaining())
            buffer.get(encryptedData)

            val json = CryptoManager.decrypt(encryptedData, iv, secretKey)
            val (access, refresh) = json.split("|", limit = 2)

            Token(
                accessToken = if (access.isNotEmpty()) access else null,
                refreshToken = if (refresh.isNotEmpty()) refresh else null
            )
        } catch (e: Exception) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: Token, output: OutputStream) {
        val json = "${t.accessToken.orEmpty()}|${t.refreshToken.orEmpty()}"
        val (encrypted, iv) = CryptoManager.encrypt(json, secretKey)

        val buffer = ByteBuffer.allocate(4 + iv.size + encrypted.size)
        buffer.putInt(iv.size)
        buffer.put(iv)
        buffer.put(encrypted)

        output.write(buffer.array())
    }
}