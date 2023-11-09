package com.example.vpassport.util.di

import KtorMediatorService
import com.example.vpassport.util.connection.ktor.AuthenticationService
import com.example.vpassport.util.connection.ktor.KtorAuthenticationService
import com.example.vpassport.util.connection.ktor.MediatorService
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WebSocketModule {

    @Provides
    @Reusable
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging)
            install(WebSockets)
        }
    }

    @Provides
    @Reusable
    fun provideMediatorService(httpClient: HttpClient): MediatorService {
        return KtorMediatorService(httpClient)
    }


    @Provides
    @Reusable
    fun provideAuthenticationService(httpClient: HttpClient): AuthenticationService {
        return KtorAuthenticationService(httpClient)
    }
}
