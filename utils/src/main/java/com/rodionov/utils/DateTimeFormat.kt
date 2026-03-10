package com.rodionov.utils

import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Объект для работы с форматированием даты и времени.
 */
object DateTimeFormat {

    private val defaultFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * Форматирует LocalDate в строку dd.MM.yyyy.
     */
    fun formatDate(date: LocalDate): String = date.format(defaultFormatter)

    /**
     * Преобразует строку даты (yyyy-MM-dd) в Long (timestamp в миллисекундах).
     */
    fun transformApiDateToLong(date: String?): Long {
        if (date.isNullOrBlank()) return 0L
        return try {
            LocalDate.parse(date, defaultFormatter)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Преобразует Long (timestamp) в строку даты для API (yyyy-MM-dd).
     */
    fun transformLongToApiDate(date: Long?): String {
        if (date == null || date == 0L) return ""
        return try {
            LocalDate.ofEpochDay(date / (24 * 60 * 60 * 1000))
                .format(apiFormatter)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Преобразует Long (timestamp) в пользовательскую строку даты (dd.MM.yyyy).
     */
    fun transformLongToDisplayDate(date: Long?): String {
        if (date == null || date == 0L) return ""
        return try {
            java.time.Instant.ofEpochMilli(date)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
                .format(defaultFormatter)
        } catch (e: Exception) {
            ""
        }
    }

}
