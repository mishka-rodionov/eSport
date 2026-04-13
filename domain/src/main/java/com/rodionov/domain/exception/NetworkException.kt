package com.rodionov.domain.exception

/**
 * Исключение сетевого запроса с кодом ошибки сервера.
 *
 * @property code Код ошибки, полученный от сервера.
 * @property message Текстовое сообщение об ошибке.
 */
class NetworkException(val code: Int, override val message: String) : Exception(message)
