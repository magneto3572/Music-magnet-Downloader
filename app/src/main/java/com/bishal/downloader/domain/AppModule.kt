package com.bishal.downloader.domain

import android.content.Context
import com.bishal.downloader.data.RemoteDataSource
import com.bishal.downloader.data.apiCall.UserApi
import com.bishal.downloader.domain.utils.NetworkUtil
import com.bishal.downloader.domain.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesAuthApi(remoteDataSource: RemoteDataSource): UserApi {
        return remoteDataSource.buildApi(UserApi::class.java)
    }

    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    fun provideNetworkUtil(@ApplicationContext context: Context) : NetworkUtil {
        return NetworkUtil(context)
    }
}