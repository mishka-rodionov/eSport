package com.rodionov.local.converters

import androidx.room.TypeConverter
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.OrienteeringDirection
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CompetitionConverters {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {
            LocalDate.parse(it, dateFormatter)
        }
    }

    // Если вы решите хранить OrienteeringDirection как String (рекомендуется)
    @TypeConverter
    fun fromOrienteeringDirection(direction: OrienteeringDirection?): String? {
        return direction?.name
    }

    @TypeConverter
    fun toOrienteeringDirection(value: String?): OrienteeringDirection? {
        return value?.let { OrienteeringDirection.valueOf(it) }
    }

    // Если вы решите хранить KindOfSport по имени (рекомендуется)
    @TypeConverter
    fun fromKindOfSport(kindOfSport: KindOfSport?): String? {
        return kindOfSport?.name
    }

    @TypeConverter
    fun toKindOfSport(name: String?): KindOfSport? {
        return name?.let { sportName ->
            KindOfSport.all.find { it.name == sportName }
        }
    }
}