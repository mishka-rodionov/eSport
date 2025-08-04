package com.rodionov.remote.di

import com.google.gson.Gson
import com.rodionov.remote.network.retrofit.ResultCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

private const val TIMEOUT_SECONDS = 60

val retrofitModule = module {
    singleOf(::Gson)
    singleOf(::retrofit)
}

private fun retrofit(gson: Gson): Retrofit {
    val builder = OkHttpClient.Builder()
    builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    val okClient = builder
        .retryOnConnectionFailure(true)
        .connectTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .build()
    val baseUrl = "https://BASE_URL"
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(ResultCallAdapterFactory())
        .client(okClient)
        .build()
}