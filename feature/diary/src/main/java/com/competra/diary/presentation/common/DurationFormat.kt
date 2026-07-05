package com.competra.diary.presentation.common

/**
 * Форматирует длительность тренировки в секундах как "Ч ч М мин С с",
 * опуская нулевые старшие/младшие разряды (кроме случая нулевой длительности целиком).
 *
 * Примеры: 45 -> "45 с", 330 -> "5 мин 30 с", 3900 -> "1 ч 5 мин", 3600 -> "1 ч".
 */
fun formatWorkoutDuration(totalSeconds: Int): String {
    if (totalSeconds <= 0) return "0 мин"

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return buildList {
        if (hours > 0) add("$hours ч")
        if (minutes > 0) add("$minutes мин")
        if (seconds > 0 || (hours == 0 && minutes == 0)) add("$seconds с")
    }.joinToString(" ")
}
