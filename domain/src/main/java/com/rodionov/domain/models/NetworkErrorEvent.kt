package com.rodionov.domain.models

/**
 * Событие сетевой ошибки для передачи из feature-модулей в MainActivity.
 *
 * @property code Код ошибки (null, если ошибка не от сервера).
 * @property message Текстовое сообщение об ошибке.
 */
data class NetworkErrorEvent(
    val code: Int?,
    val message: String?
)
