package com.bishal.downloader

import android.app.Application
import android.util.Log
import com.bishal.ytdlplibrary.YoutubeDL
import com.bishal.ytdlplibrary.YoutubeDLException
import com.yausername.aria2c.Aria2c
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        YoutubeDL.getInstance().init(this)
        Aria2c.getInstance().init(this)
        try {

        } catch (e: YoutubeDLException) {
            Log.e("LogTag", "failed to initialize youtubedl-android", e)
        }
    }
}