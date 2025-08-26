package com.rodionov.nfchelper.di

import com.rodionov.nfchelper.SportiduinoHelper
import com.rodionov.nfchelper.SportiduinoHelperImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val nfcModule = module {
    singleOf(::SportiduinoHelperImpl) bind SportiduinoHelper::class
}