package com.zhj.coffee.Center_Home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.zhj.coffee.CaffeineController.AddCaffeineRecordActivity
import com.zhj.coffee.Login_Register.LoginActivity
import com.zhj.coffee.R
import com.zhj.coffee.entity.BaseUrl
import com.zhj.coffee.webservice.CaffeineService
import com.zhj.coffee.webservice.PeopleService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.sql.Timestamp
import java.util.*
import kotlin.concurrent.thread
import android.view.SurfaceHolder.Callback as SurfaceHolderCallback

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
        val surfaceview = root.findViewById<SurfaceView>(R.id.surfaceview)
        val textview_sleeptime = root.findViewById<TextView>(R.id.textview_sleeptime)
        val button_addcaffeinerecord = root.findViewById<Button>(R.id.button_addcaffeinerecord)

        //提取ID
        val PREF_FILE_NAME = "user_info"
        val User_ID = "user_id"
        val pref = activity?.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val user_id = pref?.getString(User_ID, "").toString()


        //绘图
        val surfaceholder = surfaceview.holder
        val paint = Paint()

        val callback = object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {
                thread {
                    val canvas = surfaceholder.lockCanvas()
                    if (canvas != null) {
                        canvas.drawColor(Color.WHITE)

                        //绘制坐标轴
                        paint.setColor(Color.BLACK)
                        canvas.drawText("O", 4F, 605F, paint)
                        //纵坐标
                        canvas.drawLine(10F, 10F, 10F, 600F, paint)
                        canvas.drawText("咖啡因(mg)", 20F, 30F, paint)
                        //横坐标
                        canvas.drawLine(10F, 600F, 1000F, 600F, paint)
                        canvas.drawText("时间", 1000F, 605F, paint)
                    }
                    surfaceholder.unlockCanvasAndPost(canvas)
                }
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
            }
        }
        surfaceholder.addCallback(callback)

        thread {
            val nowtime = Date()
            val nowtimestr =
                (nowtime.year + 1900).toString() + "-" + (nowtime.month + 1).toString() + "-" + nowtime.date
            //获得今天的所有摄入记录
            val result_today = service.GetTodayCaffeineRecord(user_id, nowtimestr)
            //获得上一次摄入的状态
            val result_now = service.GetStateById(user_id)
            try {
                val response_today = result_today.execute()
                val response_now = result_now.execute()

                if (response_today.body()!!.toString().isNotEmpty()) {
                    val caffeinelist: JSONArray = JSON.parseArray(response_today.body()!!.string())
                    val caffeinenow: JSONObject =
                        JSON.parse(response_now.body()!!.string()) as JSONObject

                    var sumcaffeine = 0.0
                    for (i in 0 until caffeinelist.size) { //遍历JSONArray
                        val caffee = caffeinelist.getJSONObject(i)
                        val caffee_caffeine = caffee.getFloat("caffeine")
                        sumcaffeine += caffee_caffeine
                    }

                    //上一次摄入的时间
                    val start_time = caffeinenow.getTimestamp("time").time
                    //上一次摄入的咖啡因
                    val start_caffeine = caffeinenow.getFloat("caffeine")
                    //上一次到目前间隔的时间
                    val delta_time = (nowtime.time - start_time) / 1000.0 / 3600.0
                    //计算出现在的咖啡因量
                    val now_caffeine = start_caffeine * Math.pow(0.5, delta_time / 4.0)
                    //计算距离睡觉量的时间差
                    val delta_sleeptime = 4 * Math.log(100.0 / start_caffeine) / Math.log(0.5)

                    val sleeptime = Date((start_time + delta_sleeptime * 3600 * 1000).toLong())

                    //更新上方三行信息和下方信息
                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        textview_todaytext1.setText(
                            "当前体内咖啡因量：" + String.format(
                                "%.2f",
                                now_caffeine
                            ) + "mg"
                        )
                        textview_todaytext2.setText("今日饮用杯数：" + caffeinelist.size + "杯")
                        textview_todaytext3.setText(
                            "今日累计摄入：" + String.format(
                                "%.2f",
                                sumcaffeine
                            ) + "/400mg"
                        )

                        if (start_caffeine < 100) {//此时已经低于睡眠值了
                            textview_sleeptime.setText("此时已经低于睡眠值了,可以入睡啦")
                        } else {
                            textview_sleeptime.setText("您大约在" + (sleeptime.year+1900).toString() + "-" + String.format("%02d",sleeptime.month+1) + "-" + String.format("%02d",sleeptime.day) + " " + String.format("%02d",sleeptime.hours) + ":" + String.format("%02d",sleeptime.minutes) + "降至睡眠量")

                        }
                    }
                }

            } catch (e: IOException) {
                Looper.prepare()
                Toast.makeText(activity, "网络无法连接", Toast.LENGTH_LONG).show()
                Looper.loop()
            }
        }

        button_addcaffeinerecord.setOnClickListener {
            val intent = Intent(activity, AddCaffeineRecordActivity::class.java)
            activity?.startActivity(intent)
        }
        return root
    }
}