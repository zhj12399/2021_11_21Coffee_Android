package com.zhj.coffee.webservice

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CaffeineService {
    @POST("Caffeine/getLastMonthRecord/{user_id}")
    fun GetLastMonthRecord(@Path("user_id") user_id: String): Call<ResponseBody>

    @POST("Caffeine/getCaffeineRecordById/{user_id}")
    fun GetCaffeineRecordById(@Path("user_id") user_id: String): Call<ResponseBody>

    @POST("Caffeine/getTodayCaffeineRecord")
    fun GetTodayCaffeineRecord(
        @Query("id") user_id: String,
        @Query("time") time: String
    ): Call<ResponseBody>

    @POST("Caffeine/getStateById/{user_id}")
    fun GetStateById(@Path("user_id") user_id: String): Call<ResponseBody>
}