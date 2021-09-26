package com.zhj.coffee.Center_Home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zhj.coffee.Login_Register.LoginActivity
import com.zhj.coffee.Login_Register.ModifyNameActivity
import com.zhj.coffee.Login_Register.ModifyPasswordActivity
import com.zhj.coffee.R
import com.zhj.coffee.entity.BaseUrl
import com.zhj.coffee.webservice.PeopleService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import kotlin.concurrent.thread

class CenterFragment4 : Fragment() {

    lateinit var retrofit: Retrofit
    lateinit var service: PeopleService
    var user_id = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_center4, container, false)


        //初始化网络控件
        retrofit = Retrofit.Builder().baseUrl(BaseUrl.baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(PeopleService::class.java)

        //初始化控件
        val button_modifypwd = root.findViewById<Button>(R.id.button_modifypwd)
        val button_modifyname = root.findViewById<Button>(R.id.button_modifname)
        val button_delete = root.findViewById<Button>(R.id.button_delete)
        val button_logoff = root.findViewById<Button>(R.id.button_logoff)
        val button_about = root.findViewById<Button>(R.id.button_about)

        button_modifypwd.setOnClickListener {
            val intent = Intent(activity, ModifyPasswordActivity::class.java)
            startActivity(intent)
        }

        button_modifyname.setOnClickListener {
            val intent = Intent(activity, ModifyNameActivity::class.java)
            startActivity(intent)
        }

        button_delete.setOnClickListener {
            MaterialAlertDialogBuilder(activity!!)
                .setTitle("确定删除账户码？")
                .setMessage("删除后您的账号将不会被保留\n其它信息也将会被删除")
                .setNeutralButton("取消") { dialog, which ->

                }
                .setPositiveButton("是") { dialog, which ->
                    //将下线信息记录
                    val PREF_FILE_NAME = "user_info"
                    val User_ID = "user_id"
                    val Is_Login = "is_login"

                    val pref = activity?.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
                    val editor = pref?.edit()
                    editor?.putBoolean(Is_Login, false)
                    editor?.putString(User_ID, "")
                    editor?.apply()

                    thread {
                        val result = service.DeletePeopleById(user_id)
                        try {
                            result.execute()
                        } catch (e: IOException) {
                            Looper.prepare()
                            Toast.makeText(activity, "网络无法连接", Toast.LENGTH_LONG).show()
                            Looper.loop()
                        }
                    }

                    Toast.makeText(activity, "成功删除账户，欢迎再次使用", Toast.LENGTH_LONG).show()
                    val intent = Intent(activity, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity?.startActivity(intent)
                }
                .show()
        }

        button_logoff.setOnClickListener {
            MaterialAlertDialogBuilder(activity!!)
                .setTitle("确定注销账户码？")
                .setMessage("退出后您的账号仍会保留\n其它信息也不会被删除")
                .setNeutralButton("取消") { dialog, which ->

                }
                .setPositiveButton("是") { dialog, which ->
                    //将下线信息记录
                    val PREF_FILE_NAME = "user_info"
                    val User_ID = "user_id"
                    val Is_Login = "is_login"

                    val pref = activity?.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
                    val editor = pref?.edit()
                    editor?.putBoolean(Is_Login, false)
                    editor?.putString(User_ID, "")
                    editor?.apply()

                    Toast.makeText(activity, "成功下线", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity?.startActivity(intent)
                }
                .show()
        }

        button_about.setOnClickListener {
            MaterialAlertDialogBuilder(activity!!)
                .setTitle("关于咖啡世界")
                .setMessage(
                    "一款可记录咖啡因的网站\n所有数据均来自于公布数据\n" +
                            "由于个人体质等条件不同，数据仅供参考\n" +
                            "作者:zhj12399\n" +
                            "作者邮箱:zhj727534681@163.com\n" +
                            "网站地址:zhj12399.cn:9000"
                )
                .setPositiveButton("确定") { dialog, which ->
                }
                .show()
        }
        return root
    }

    override fun onStart() {
        super.onStart()

        val textview_userid = view?.findViewById<TextView>(R.id.textview_userid)
        val textview_username = view?.findViewById<TextView>(R.id.textview_oldusername)

        //提取ID
        val PREF_FILE_NAME = "user_info"
        val User_ID = "user_id"
        val pref = getActivity()?.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        user_id = pref?.getString(User_ID, "").toString()
        textview_userid?.setText("ID：$user_id")
        //得到昵称
        thread {
            val result = service.GetNameById(user_id)
            try {
                val response = result.execute()
                val user_name = response.body()!!.string()
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post {
                    textview_username?.setText("您好，$user_name")
                }
            } catch (e: IOException) {
                Looper.prepare()
                Toast.makeText(activity, "网络无法连接", Toast.LENGTH_LONG).show()
                Looper.loop()
            }
        }
    }
}