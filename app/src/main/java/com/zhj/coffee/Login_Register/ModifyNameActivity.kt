package com.zhj.coffee.Login_Register

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.zhj.coffee.R
import com.zhj.coffee.entity.BaseUrl
import com.zhj.coffee.webservice.PeopleService
import kotlinx.android.synthetic.main.activity_modify_name.*
import kotlinx.android.synthetic.main.activity_modify_password.button_Modify
import kotlinx.android.synthetic.main.activity_modify_password.textview_oldusername
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import kotlin.concurrent.thread

class ModifyNameActivity : AppCompatActivity() {

    lateinit var retrofit: Retrofit
    lateinit var service: PeopleService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_name)

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
        var user_name = ""
        thread {
            val result = service.GetNameById(user_id)
            try {
                val response = result.execute()
                user_name = response.body()!!.string().toString()
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post {
                    textview_oldusername.setText("现在使用的昵称：$user_name")
                }
            } catch (e: IOException) {
                Looper.prepare()
                Toast.makeText(this, "网络无法连接", Toast.LENGTH_LONG).show()
                Looper.loop()
            }
        }

        button_Modify.setOnClickListener {
            if (edittext_newusername.text.isNotEmpty() && edittext_newusername.text.toString() != user_name) {
                thread {
                    val result =
                        service.UpdateNameById(user_id, edittext_newusername.text.toString())
                    try {
                        val response = result.execute()
                        val isModify = response.body()!!.string().toBoolean()
                        if (isModify) {
                            Looper.prepare()
                            Toast.makeText(this, "修改成功", Toast.LENGTH_LONG).show()
                            finish()
                            Looper.loop()
                        } else {
                            Looper.prepare()
                            Toast.makeText(this, "昵称已被使用，修改失败", Toast.LENGTH_LONG).show()
                            Looper.loop()
                        }

                    } catch (e: IOException) {
                        Looper.prepare()
                        Toast.makeText(this, "网络无法连接", Toast.LENGTH_LONG).show()
                        Looper.loop()
                    }
                }
            } else {
                Toast.makeText(this, "昵称未修改", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}