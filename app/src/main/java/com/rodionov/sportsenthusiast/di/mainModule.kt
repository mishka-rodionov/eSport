package com.rodionov.sportsenthusiast.di

import com.rodionov.sportsenthusiast.presentation.main.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainModule = module {
    viewModelOf(::MainViewModel)
}