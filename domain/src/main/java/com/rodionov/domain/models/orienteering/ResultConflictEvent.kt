package com.rodionov.domain.models.orienteering

/**
 * Событие конфликта результата: в БД уже есть запись для участника,
 * а с чипа пришли новые данные.
 *
 * @property participantName Отображаемое имя участника.
 * @property existingResult Результат, сохранённый в БД.
 * @property newResult Результат, только что считанный с чипа.
 * @property onApply Suspend-колбэк, вызываемый при нажатии «Применить».
 */
class ResultConflictEvent(
    val participantName: String,
    val existingResult: OrienteeringResult,
    val newResult: OrienteeringResult,
    val onApply: suspend () -> Unit
)
