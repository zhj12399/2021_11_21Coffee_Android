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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.concurrent.thread

class CenterFragment3 : Fragment() {

    lateinit var retrofit: Retrofit
    lateinit var service: CaffeineService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_center3, container, false)

        //初始化网络控件
        retrofit = Retrofit.Builder().baseUrl(BaseUrl.baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(CaffeineService::class.java)

        return root
    }

    override fun onStart() {
        super.onStart()

        //初始化控件
        val textview_weektext1 = view?.findViewById<TextView>(R.id.textview_weektext1)
        val textview_weektext2 = view?.findViewById<TextView>(R.id.textview_weektext2)
        val textview_monthtext1 = view?.findViewById<TextView>(R.id.textview_monthtext1)
        val textview_monthtext2 = view?.findViewById<TextView>(R.id.textview_monthtext2)
        //提取ID
        val PREF_FILE_NAME = "user_info"
        val User_ID = "user_id"
        val pref = getActivity()?.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val user_id = pref?.getString(User_ID, "").toString()
        thread {
            val result = service.GetLastMonthRecord(user_id)
            val nowtime = LocalDateTime.now()

            var LastWeekSum = 0.0
            var LastMonthSum = 0.0
            try {
                val response = result.execute()
                val caffeinelist: JSONArray = JSON.parseArray(response.body()!!.string())
                for (i in 0 until caffeinelist.size) { //遍历JSONArray
                    val caffee = caffeinelist.getJSONObject(i)
                    val caffee_time = caffee.getDate("time")
                    val caffee_caffeine = caffee.getDouble("caffeine")
                    //计算当前时间和记录的差值
                    val chazhi =
                        nowtime.toInstant(ZoneOffset.of("+8")).toEpochMilli() - caffee_time.time
                    if (chazhi < 7 * 24 * 3600 * 1000 && chazhi > 0) {
                        LastWeekSum += caffee_caffeine
                    }
                    LastMonthSum += caffee_caffeine
                }

                val LastWeekAvg = String.format("%.2f", LastWeekSum / 7)
                val LastMonthAvg = String.format("%.2f", LastMonthSum / 30)
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post {
                    textview_weektext1?.setText("本周一共饮用：" + LastWeekSum + "mg")
                    textview_weektext2?.setText("本周平均饮用：" + LastWeekAvg + "mg")
                    textview_monthtext1?.setText("本月一共饮用：" + LastMonthSum + "mg")
                    textview_monthtext2?.setText("本月平均饮用：" + LastMonthAvg + "mg")

                }
            } catch (e: IOException) {
                Looper.prepare()
                Toast.makeText(activity, "网络无法连接", Toast.LENGTH_LONG).show()
                Looper.loop()
            }
        }
    }
}