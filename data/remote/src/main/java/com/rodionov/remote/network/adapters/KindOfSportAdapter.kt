package com.rodionov.remote.network.adapters
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.rodionov.domain.models.KindOfSport

class KindOfSportAdapter : TypeAdapter<KindOfSport>() {
    override fun write(out: JsonWriter, value: KindOfSport?) {
        out.value(
            when (value) {
                is KindOfSport.Orienteering -> "orienteering"
                is KindOfSport.CrossCountrySki -> "cross_country_ski"
                is KindOfSport.TrailRunning -> "trail_running"
                null -> null
            }
        )
    }

    override fun read(reader: JsonReader): KindOfSport? {
        val value = reader.nextString()
        return KindOfSport.fromSerializedName(value)
    }
}