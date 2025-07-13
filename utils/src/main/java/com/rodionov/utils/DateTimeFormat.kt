package com.rodionov.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateTimeFormat {

    fun formatDate(date: LocalDate) = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

}