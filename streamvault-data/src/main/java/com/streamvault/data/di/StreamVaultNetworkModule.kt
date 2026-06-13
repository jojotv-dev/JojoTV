package com.streamvault.data.di

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.streamvault.data.remote.NetworkTimeoutConfig
import com.streamvault.data.remote.http.DefaultUserAgentInterceptor
import com.streamvault.data.remote.http.buildAppRequestProfile
import com.streamvault.data.remote.http.buildAppUserAgent
import com.streamvault.data.remote.stalker.OkHttpStalkerApiService
import com.streamvault.data.remote.stalker.StalkerApiService
import com.streamvault.data.remote.xtream.XtreamApiService
import com.streamvault.data.remote.xtream.OkHttpXtreamApiService
import com.streamvault.data.remote.xtream.XtreamUrlFactory
import com.streamvault.data.parser.XmltvParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StreamVaultNetworkModule {

    @Provides
    @Singleton
    fun provideXtreamApiService(okHttpClient: OkHttpClient, json: Json): XtreamApiService =
        OkHttpXtreamApiService(
            client = okHttpClient,
            json = json,
            defaultRequestProfile = buildAppRequestProfile("jojotv", ownerTag = "app/xtream")
        )

    @Provides
    @Singleton
    fun provideStalkerApiService(okHttpClient: OkHttpClient, json: Json): StalkerApiService =
        OkHttpStalkerApiService(okHttpClient, json)

    @Provides
    @Singleton
    fun provideXmltvParser(): XmltvParser = XmltvParser()

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()
    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true; isLenient = true; explicitNulls = false; coerceInputValues = true }
}
