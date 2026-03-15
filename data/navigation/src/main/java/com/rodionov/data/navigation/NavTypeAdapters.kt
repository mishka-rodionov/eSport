package com.rodionov.data.navigation

import android.os.Bundle
import android.net.Uri
import androidx.navigation.NavType
import com.rodionov.domain.models.cyclic_event.EventParticipantGroup
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val EventParticipantGroupNavType = object : NavType<EventParticipantGroup>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): EventParticipantGroup? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): EventParticipantGroup {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun put(bundle: Bundle, key: String, value: EventParticipantGroup) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun serializeAsValue(value: EventParticipantGroup): String {
        return Uri.encode(Json.encodeToString(value))
    }
}
