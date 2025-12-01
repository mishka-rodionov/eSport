package com.rodionov.data.navigation

import androidx.navigation.NavOptionsBuilder

interface BaseNavigation{
    var navOptionsBuilder: (NavOptionsBuilder.() -> Unit)?
}


