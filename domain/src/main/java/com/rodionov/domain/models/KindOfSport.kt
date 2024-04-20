package com.rodionov.domain.models

sealed class KindOfSport(val name: String) {

    class Orienteering: KindOfSport("Orienteering")

}