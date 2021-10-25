package com.zhj.coffee.webservice

import com.zhj.coffee.entity.People
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PeopleService {
    @POST("People/ExistPeople/{user_id}")
    fun ExistPeople(@Path("user_id") user_id: String): Call<ResponseBody>

    @POST("People/JudgePassword")
    fun JudgePassword(@Body logininfo: People): Call<ResponseBody>

    @POST("People/AddPeople")
    fun AddPeople(@Body registerinfo: People): Call<ResponseBody>

    @POST("People/getNameById/{user_id}")
    fun GetNameById(@Path("user_id") user_id: String): Call<ResponseBody>

    @POST("People/updatePasswordById")
    fun UpdatePasswordById(
        @Query("id") user_id: String,
        @Query("password") newpassword: String
    ): Call<ResponseBody>

    @POST("People/updateNameById")
    fun UpdateNameById(
        @Query("id") user_id: String,
        @Query("name") newname: String
    ): Call<ResponseBody>

    @POST("People/deletePeopleById")
    fun DeletePeopleById(@Query("id") user_id: String): Call<ResponseBody>
}