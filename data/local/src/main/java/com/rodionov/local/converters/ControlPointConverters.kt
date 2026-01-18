package com.rodionov.local.converters

import androidx.room.TypeConverter
import com.rodionov.domain.models.orienteering.ControlPoint
import com.rodionov.domain.models.orienteering.ControlPointRole
import kotlinx.serialization.json.Json

class ControlPointConverters {
    // Используем Kotlinx Serialization для JSON
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false // Не кодируем значения по умолчанию для экономии места
        prettyPrint = false
    }

    // Для ControlPointRole enum
    @TypeConverter
    fun fromControlPointRole(role: ControlPointRole?): String? {
        return role?.name
    }

    @TypeConverter
    fun toControlPointRole(value: String?): ControlPointRole? {
        return if (value.isNullOrEmpty()) {
            ControlPointRole.ORDINARY
        } else {
            try {
                ControlPointRole.valueOf(value)
            } catch (e: IllegalArgumentException) {
                ControlPointRole.ORDINARY
            }
        }
    }

    // Для одиночного ControlPoint
    @TypeConverter
    fun fromControlPoint(controlPoint: ControlPoint?): String? {
        return controlPoint?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toControlPoint(jsonString: String?): ControlPoint? {
        return jsonString?.let {
            try {
                json.decodeFromString<ControlPoint>(it)
            } catch (e: Exception) {
                // Fallback: пытаемся распарсить только номер
                val number = it.toIntOrNull() ?: 0
                ControlPoint(number = number)
            }
        }
    }

    // Для List<ControlPoint>
    @TypeConverter
    fun fromControlPointList(controlPoints: List<ControlPoint>?): String? {
        return controlPoints?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toControlPointList(jsonString: String?): List<ControlPoint>? {
        return jsonString?.let {
            try {
                json.decodeFromString<List<ControlPoint>>(it)
            } catch (e: Exception) {
                // Fallback: создаем список из номеров
                val numbers = it.split(",").mapNotNull { numStr ->
                    numStr.trim().toIntOrNull()?.let { number ->
                        ControlPoint(number = number)
                    }
                }
                numbers
            }
        }
    }

    // Для Map<Int, ControlPoint> (например, ключ - номер КП)
    @TypeConverter
    fun fromControlPointMap(map: Map<Int, ControlPoint>?): String? {
        return map?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toControlPointMap(jsonString: String?): Map<Int, ControlPoint>? {
        return jsonString?.let {
            try {
                json.decodeFromString<Map<Int, ControlPoint>>(it)
            } catch (e: Exception) {
                emptyMap()
            }
        }
    }

    // Для Set<ControlPoint> (уникальные КП)
    @TypeConverter
    fun fromControlPointSet(set: Set<ControlPoint>?): String? {
        return set?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toControlPointSet(jsonString: String?): Set<ControlPoint>? {
        return jsonString?.let {
            try {
                json.decodeFromString<Set<ControlPoint>>(it)
            } catch (e: Exception) {
                emptySet()
            }
        }
    }
}