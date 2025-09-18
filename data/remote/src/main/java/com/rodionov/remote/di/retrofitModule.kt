package com.rodionov.remote.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rodionov.domain.models.KindOfSport
import com.rodionov.domain.repository.auth.TokenRepository
import com.rodionov.remote.datasource.auth.AuthRemoteDataSource
import com.rodionov.remote.interceptors.MockInterceptor
import com.rodionov.remote.network.adapters.KindOfSportAdapter
import com.rodionov.remote.network.interceptors.AuthInterceptor
import com.rodionov.remote.network.retrofit.ResultCallAdapterFactory
import com.rodionov.remote.network.retrofit.TokenAuthenticator
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
    singleOf(::createGson)
    singleOf(::retrofit)
}

fun retrofit(
    gson: Gson,
    tokenRepository: TokenRepository,
    context: Context
): Retrofit {
    val builder = OkHttpClient.Builder()
    val collector = ChuckerCollector(context, true)
    val interceptor = ChuckerInterceptor
        .Builder(context)
        .collector(collector)
        .build()
    builder.addInterceptor(interceptor)
    builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    val okClient = builder
        .addInterceptor(AuthInterceptor(tokenRepository = tokenRepository))
        .authenticator(TokenAuthenticator(tokenRepository = tokenRepository))
        .addInterceptor(MockInterceptor())
        .retryOnConnectionFailure(true)
        .connectTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .build()
    val baseUrl = "https://BASE_URL"
    val localBaseUrl = "http://192.168.1.113:8080/"
    return Retrofit.Builder()
        .baseUrl(localBaseUrl)
//        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(ResultCallAdapterFactory())
        .client(okClient)
        .build()
}

private fun createGson(): Gson {
    return GsonBuilder()
        .registerTypeAdapter(KindOfSport::class.java, KindOfSportAdapter())
        .create()
}