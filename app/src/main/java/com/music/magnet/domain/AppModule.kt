package com.music.magnet.domain

import android.content.Context
import com.music.magnet.domain.utils.NetworkUtil
import com.music.magnet.domain.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    fun provideNetworkUtil(@ApplicationContext context: Context) : NetworkUtil {
        return NetworkUtil(context)
    }
}