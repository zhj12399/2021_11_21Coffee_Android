package com.zhj.coffee.Login_Register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import com.zhj.coffee.R
import com.zhj.coffee.entity.BaseUrl
import com.zhj.coffee.webservice.PeopleService
import kotlinx.android.synthetic.main.activity_find_password.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import kotlin.concurrent.thread

class FindPasswordActivity : AppCompatActivity() {

    lateinit var retrofit: Retrofit
    lateinit var service: PeopleService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)

        retrofit = Retrofit.Builder().baseUrl(BaseUrl.baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(PeopleService::class.java)

        button_findpassword.setOnClickListener {
            if (edittext_findpassword_username.text.isNotEmpty()) {
                thread {
                    val result =
                        service.SentPasswordMail(edittext_findpassword_username.text.toString())
                    try {
                        val response = result.execute()
                        val isModify = response.body()!!.string().toBoolean()
                        if (isModify) {
                            Looper.prepare()
                            Toast.makeText(this, "您的密码已发送至您的邮箱", Toast.LENGTH_LONG).show()
                            finish()
                            Looper.loop()
                        } else {
                            Looper.prepare()
                            Toast.makeText(this, "查无此人", Toast.LENGTH_LONG).show()
                            Looper.loop()
                        }

                    } catch (e: IOException) {
                        Looper.prepare()
                        Toast.makeText(this, "网络无法连接", Toast.LENGTH_LONG).show()
                        Looper.loop()
                    }
                }
            } else {
                Toast.makeText(this, "请填写您要找回密码的用户名", Toast.LENGTH_LONG).show()
            }
        }
    }
}