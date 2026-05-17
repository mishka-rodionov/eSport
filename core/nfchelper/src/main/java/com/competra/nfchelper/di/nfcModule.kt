package com.competra.nfchelper.di

import com.competra.nfchelper.SportiduinoHelper
import com.competra.nfchelper.SportiduinoHelperImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val nfcModule = module {
    singleOf(::SportiduinoHelperImpl) bind SportiduinoHelper::class
}