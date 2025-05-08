package com.rodionov.data.navigation.di

import com.rodionov.data.navigation.Navigation
import com.rodionov.data.navigation.NavigationImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val navigationModule = module {
    singleOf<Navigation>(::NavigationImpl)
}