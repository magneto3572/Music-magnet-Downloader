package com.bishal.downloader.data.repository

import com.bishal.downloader.data.apiCall.SafeApiCall
import com.bishal.downloader.data.apiCall.UserApi
import javax.inject.Inject


class HomeRepository @Inject constructor(private val api: UserApi): SafeApiCall {
//
//    suspend fun getCategory(num: JsonObject) = safeApiCall {
////        api.getCategory (num)
//    }
}