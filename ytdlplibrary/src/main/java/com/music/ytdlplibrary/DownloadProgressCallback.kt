package com.music.ytdlplibrary

interface DownloadProgressCallback {
    fun onProgressUpdate(progress: Float, etaInSeconds: Long, line: String?)
}