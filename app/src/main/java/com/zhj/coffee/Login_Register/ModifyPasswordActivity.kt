package com.zhj.coffee.Login_Register

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.zhj.coffee.R
import com.zhj.coffee.entity.BaseUrl
import com.zhj.coffee.webservice.PeopleService
import kotlinx.android.synthetic.main.activity_modify_password.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import kotlin.concurrent.thread

class ModifyPasswordActivity : AppCompatActivity() {

    lateinit var retrofit: Retrofit
    lateinit var service: PeopleService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_password)

        //初始化网络控件
        retrofit = Retrofit.Builder().baseUrl(BaseUrl.baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(PeopleService::class.java)

        //提取ID
        val PREF_FILE_NAME = "user_info"
        val User_ID = "user_id"
        val pref = this.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val user_id = pref?.getString(User_ID, "").toString()

        thread {
            val result = service.GetNameById(user_id)
            try {
                val response = result.execute()
                val user_name = response.body()!!.string()
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post {
                    textview_oldusername.setText("昵称：$user_name")
                }
            } catch (e: IOException) {
                Looper.prepare()
                Toast.makeText(this, "网络无法连接", Toast.LENGTH_LONG).show()
                Looper.loop()
            }
        }

        button_Modify.setOnClickListener {
            if (edittext_newpassword.text.isNotEmpty()) {
                thread {
                    val result = service.UpdatePasswordById(user_id, edittext_newpassword.text.toString())
                    try {
                        result.execute()
                        Looper.prepare()
                        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
                        finish()
                        Looper.loop()
                    } catch (e: IOException) {
                        Looper.prepare()
                        Toast.makeText(this, "网络无法连接", Toast.LENGTH_LONG).show()
                        Looper.loop()
                    }
                }
            } else {
                Toast.makeText(this, "密码未修改", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}