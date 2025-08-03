package com.rodionov.domain.models

sealed class KindOfSport(val name: String) {

    data object Orienteering: KindOfSport("Orienteering")

    data object CrossCountrySki: KindOfSport("CrossCountrySki")

    data object TrailRunning : KindOfSport("TrailRunning")

    companion object {
        val all = listOf(Orienteering, CrossCountrySki, TrailRunning)

        fun fromName(name: String): KindOfSport? =
            all.find { it.name == name }

    }

}