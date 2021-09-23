package com.zhj.coffee.Center_Home

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
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

        //提取ID
        val PREF_FILE_NAME = "user_info"
        val User_ID = "user_id"
        val pref = activity?.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val user_id = pref?.getString(User_ID, "").toString()

        val surfaceholder = surfaceview.holder
        val paint =Paint()
        val callback = object:SurfaceHolder.Callback{
            override fun surfaceCreated(p0: SurfaceHolder) {
                thread {
                    val canvas = surfaceholder.lockCanvas()

                    if (canvas != null) {
                        canvas.drawColor(Color.WHITE)
                        //绘制坐标轴
                        paint.setColor(Color.BLACK);
                        canvas.drawText("O", 20F, 220F, paint);

                        paint.setColor(Color.BLACK);
                        canvas.drawLine(10F, 10F, 10F, 480F, paint)
                        canvas.drawText("Y", 20F, 30F, paint)

                        paint.setColor(Color.WHITE);
                        canvas.drawLine(0F, 240F, 320F, 240F, paint);
                        canvas.drawText("X", 300F, 260F, paint);
                    }
                    surfaceholder.unlockCanvasAndPost(canvas);
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
            val result_today = service.GetTodayCaffeineRecord(user_id, nowtimestr)
            val result_now = service.GetCaffeineRecordById(user_id)
            try {
                val response_today = result_today.execute()
                val response_now = result_now.execute()
                val caffeinelist: JSONArray = JSON.parseArray(response_today.body()!!.string())
                val caffeinenow: JSONArray = JSON.parseArray(response_now.body()!!.string())

                var sumcaffeine = 0.0
                for (i in 0 until caffeinelist.size) { //遍历JSONArray
                    val caffee = caffeinelist.getJSONObject(i)
                    val caffee_caffeine = caffee.getFloat("caffeine")
                    sumcaffeine += caffee_caffeine
                }

                caffeinenow.getJSONObject(0)


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