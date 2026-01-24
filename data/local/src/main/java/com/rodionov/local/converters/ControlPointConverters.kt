package com.rodionov.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rodionov.domain.models.orienteering.ControlPoint
import com.rodionov.domain.models.orienteering.ControlPointRole

class ControlPointConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromControlPointRole(role: ControlPointRole?): String? {
        return role?.name
    }

    @TypeConverter
    fun toControlPointRole(value: String?): ControlPointRole? {
        return value?.let {
            try {
                ControlPointRole.valueOf(it)
            } catch (e: Exception) {
                ControlPointRole.ORDINARY
            }
        }
    }

    @TypeConverter
    fun fromControlPoint(controlPoint: ControlPoint?): String? {
        return controlPoint?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toControlPoint(jsonString: String?): ControlPoint? {
        return jsonString?.let {
            gson.fromJson(it, ControlPoint::class.java)
        }
    }

    @TypeConverter
    fun fromControlPointList(controlPoints: List<ControlPoint>?): String? {
        return controlPoints?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toControlPointList(jsonString: String?): List<ControlPoint>? {
        return jsonString?.let {
            val listType = object : TypeToken<List<ControlPoint>>() {}.type
            gson.fromJson(it, listType)
        }
    }

    @TypeConverter
    fun fromControlPointMap(map: Map<Int, ControlPoint>?): String? {
        return map?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toControlPointMap(jsonString: String?): Map<Int, ControlPoint>? {
        return jsonString?.let {
            val mapType = object : TypeToken<Map<Int, ControlPoint>>() {}.type
            gson.fromJson(it, mapType)
        }
    }

    @TypeConverter
    fun fromControlPointSet(set: Set<ControlPoint>?): String? {
        return set?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toControlPointSet(jsonString: String?): Set<ControlPoint>? {
        return jsonString?.let {
            val setType = object : TypeToken<Set<ControlPoint>>() {}.type
            gson.fromJson(it, setType)
        }
    }
}
