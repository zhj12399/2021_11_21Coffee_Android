package com.zhj.coffee

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zhj.coffee.Center_Home.CenterActivity
import com.zhj.coffee.Login_Register.FirstStartAppAdpater
import com.zhj.coffee.Login_Register.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isFirstStartApp()) {
            pager.adapter = FirstStartAppAdpater(this)
        } else {
            if (isLogin()) {//有登录的账号
                val intent = Intent(this,CenterActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                val intent = Intent(this,LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    //判断是否为第一次打开App，True代表第一次打开
    fun isFirstStartApp(): Boolean {
        val PREF_FILE_NAME = "user_info"
        val First_Login = "first_login"

        val pref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val state = pref.getBoolean(First_Login, true)
        if (state) {//第一次打开App，并标记为false
            val editor = pref.edit()
            editor.putBoolean(First_Login, false)
            editor.apply()
            return true
        } else {
            return false
        }
    }

    fun isLogin(): Boolean {
        val PREF_FILE_NAME = "user_info"
        val Is_Login = "is_login"

        val pref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        return pref.getBoolean(Is_Login, false)
    }
}