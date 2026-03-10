package com.rodionov.local.converters

import androidx.room.TypeConverter
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.models.orienteering.OrienteeringDirection
import com.rodionov.domain.models.orienteering.PunchingSystem

/**
 * Конвертеры типов для хранения объектов соревнований в Room.
 * 
 * Примечание: Конвертеры для LocalDate удалены, так как дата теперь хранится как Long.
 */
class CompetitionConverters {

    @TypeConverter
    fun fromOrienteeringDirection(direction: OrienteeringDirection?): String? {
        return direction?.name
    }

    @TypeConverter
    fun toOrienteeringDirection(value: String?): OrienteeringDirection? {
        return value?.let { OrienteeringDirection.valueOf(it) }
    }

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

    @TypeConverter
    fun fromPunchingSystem(punchingSystem: PunchingSystem?): String? {
        return punchingSystem?.name
    }

    @TypeConverter
    fun toPunchingSystem(value: String?): PunchingSystem? {
        return value?.let { PunchingSystem.valueOf(it) }
    }
}
