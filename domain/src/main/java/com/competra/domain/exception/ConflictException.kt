package com.competra.domain.exception

/**
 * Исключение конфликта при попытке записать на сервер устаревшие данные.
 *
 * Кидается из сетевого слоя, когда сервер возвращает HTTP 409 в ответ на upsert —
 * это значит, что серверная запись была изменена другим источником позже, чем
 * клиент в последний раз успешно синхронизировался.
 *
 * Согласно политике server-wins, потребитель (SyncCenterWorker) обязан:
 * 1. Распарсить [serverPayload] в актуальный response-DTO нужного типа,
 * 2. Перезаписать локальную запись данными с сервера (markUnsynced=false),
 * 3. Поставить isSynced=true и serverUpdatedAt из ответа.
 *
 * @property httpCode HTTP-код ответа (всегда 409).
 * @property serverPayload Сырое тело ответа сервера в виде JSON-строки. Может быть null,
 *   если сервер не вернул тело.
 */
class ConflictException(
    val httpCode: Int,
    val serverPayload: String?
) : Exception("HTTP $httpCode: server-side record is newer")
