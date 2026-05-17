package com.competra.remote.response.mappers

import com.competra.domain.models.auth.Token
import com.competra.remote.response.auth.TokenResponse

fun TokenResponse.toDomain(): Token {
    return Token(accessToken = accessToken, refreshToken = refreshToken)
}