package com.competra.remote.di

import com.competra.remote.datasource.device.DeviceRemoteDataSource
import com.competra.remote.extension.singleRemoteDataSourceOf
import org.koin.dsl.module

val deviceModule = module {
    singleRemoteDataSourceOf(DeviceRemoteDataSource::class.java)
}
