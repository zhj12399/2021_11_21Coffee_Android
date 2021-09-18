package com.zhj.coffee.webservice

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

interface CaffeineService {
    @POST("Caffeine/getLastMonthRecord/{user_id}")
    fun GetLastMonthRecord(@Path("user_id") user_id: String): Call<ResponseBody>

    @POST("Caffeine/getCaffeineRecordById/{user_id}")
    fun GetCaffeineRecordById(@Path("user_id") user_id: String): Call<ResponseBody>
}