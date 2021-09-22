package com.zhj.coffee.Center_Home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.zhj.coffee.R
import com.zhj.coffee.entity.BaseUrl
import com.zhj.coffee.webservice.CaffeineService
import com.zhj.coffee.webservice.PeopleService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread

class CenterFragment1 : Fragment() {

    lateinit var retrofit: Retrofit
    lateinit var service: CaffeineService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_center1, container, false)

        //初始化网络控件
        retrofit = Retrofit.Builder().baseUrl(BaseUrl.baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(CaffeineService::class.java)

        //初始化控件
        val textview_todaytext1 = root.findViewById<TextView>(R.id.textview_todaytext1)
        val textview_todaytext2 = root.findViewById<TextView>(R.id.textview_todaytext2)
        val textview_todaytext3 = root.findViewById<TextView>(R.id.textview_todaytext3)

        //提取ID
        val PREF_FILE_NAME = "user_info"
        val User_ID = "user_id"
        val pref = getActivity()?.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val user_id = pref?.getString(User_ID, "").toString()

        thread {
            val nowtime = Date()
            val nowtimestr =
                (nowtime.year + 1900).toString() + "-" + (nowtime.month + 1).toString() + "-" + nowtime.date
            val result_today = service.GetTodayCaffeineRecord(user_id, nowtimestr)
            val result_now = service.GetCaffeineRecordById(user_id)
            try {
                val response_today = result_today.execute()
                val response_now = result_now.execute()
                val caffeinelist: JSONArray = JSON.parseArray(response_today.body()!!.string())

                var sumcaffeine = 0.0
                for (i in 0 until caffeinelist.size) { //遍历JSONArray
                    val caffee = caffeinelist.getJSONObject(i)
                    val caffee_caffeine = caffee.getFloat("caffeine")
                    sumcaffeine += caffee_caffeine
                }
                val mainHandler = Handler(Looper.getMainLooper())

                mainHandler.post {
                    textview_todaytext2.setText("今日饮用杯数：" + caffeinelist.size + "杯")
                    textview_todaytext3.setText("今日累计摄入：" + sumcaffeine + "/400mg")
                }
            } catch (e: IOException) {
                Looper.prepare()
                Toast.makeText(activity, "网络无法连接", Toast.LENGTH_LONG).show()
                Looper.loop()
            }
        }
        return root
    }
}