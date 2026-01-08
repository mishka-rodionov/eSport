package com.rodionov.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rodionov.domain.models.ResultStatus
import com.rodionov.domain.models.orienteering.SplitTime

class ResultConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromResultStatus(status: ResultStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toResultStatus(value: String?): ResultStatus? {
        return value?.let { ResultStatus.valueOf(it) }
    }

    @TypeConverter
    fun fromSplitTimeList(splits: List<SplitTime>?): String? {
        return if (splits == null) null else gson.toJson(splits)
    }

    @TypeConverter
    fun toSplitTimeList(value: String?): List<SplitTime>? {
        if (value == null) return null
        val listType = object : TypeToken<List<SplitTime>>() {}.type
        return gson.fromJson(value, listType)
    }
}
