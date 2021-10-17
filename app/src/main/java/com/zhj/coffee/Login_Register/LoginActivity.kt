package com.zhj.coffee.Login_Register

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import com.zhj.coffee.Center_Home.CenterActivity
import com.zhj.coffee.R
import com.zhj.coffee.entity.BaseUrl.Companion.baseurl
import com.zhj.coffee.entity.People
import com.zhj.coffee.webservice.PeopleService
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {

    lateinit var retrofit: Retrofit
    lateinit var service: PeopleService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        retrofit = Retrofit.Builder().baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(PeopleService::class.java)

        button_Login.setOnClickListener {
            val user_name = textview_oldusername.text.toString()
            val user_password = edittext_userpassword.text.toString()
            if (user_name.isNotEmpty() && user_password.isNotEmpty()) {
                thread {
                    val result = service.JudgePassword(People(0, user_name, user_password))
                    try {
                        val response = result.execute()
                        val user_id = response.body()!!.string()
                        if (user_id != "0") {//登录成功
                            //将ID值写入缓存
                            val PREF_FILE_NAME = "user_info"
                            val User_ID = "user_id"
                            val Is_Login = "is_login"

                            val pref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
                            val editor = pref.edit()
                            editor.putBoolean(Is_Login, true)
                            editor.putString(User_ID, user_id)
                            editor.apply()

                            Looper.prepare()
                            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
                            //启动主界面
                            val intent = Intent(this, CenterActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            Looper.loop()
                        } else {//密码错误
                            Looper.prepare()
                            Toast.makeText(this, "账户或密码错误", Toast.LENGTH_LONG).show()
                            Looper.loop()

                            edittext_userpassword.setText("")
                        }
                    } catch (e: IOException) {
                        Looper.prepare()
                        Toast.makeText(this, "网络无法连接", Toast.LENGTH_LONG).show()
                        Looper.loop()
                    }
                }
            } else {
                Toast.makeText(this, "账户密码不能为空", Toast.LENGTH_SHORT).show()
            }
        }

        button_Register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            this.startActivity(intent)
        }
    }
}