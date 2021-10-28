package com.zhj.coffee.Center_Home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import java.util.*
import kotlin.concurrent.thread

class CenterFragment1 : Fragment() {

    lateinit var caffeine_retrofit: Retrofit
    lateinit var caffeine_service: CaffeineService
    lateinit var people_retrofit: Retrofit
    lateinit var people_service: PeopleService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_center1, container, false)

        val button_addcaffeinerecord = root.findViewById<Button>(R.id.button_addcaffeinerecord)

        //初始化网络控件
        caffeine_retrofit = Retrofit.Builder().baseUrl(BaseUrl.baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        caffeine_service = caffeine_retrofit.create(CaffeineService::class.java)
        people_retrofit = Retrofit.Builder().baseUrl(BaseUrl.baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        people_service = people_retrofit.create(PeopleService::class.java)

        button_addcaffeinerecord.setOnClickListener {
            val intent = Intent(activity, AddCaffeineRecordActivity::class.java)
            activity?.startActivity(intent)
        }
        return root
    }

    override fun onStart() {
         super.onStart()
         //初始化控件
         val textview_todaytext1 = view?.findViewById<TextView>(R.id.textview_todaytext1)
         val textview_todaytext2 = view?.findViewById<TextView>(R.id.textview_todaytext2)
         val textview_todaytext3 = view?.findViewById<TextView>(R.id.textview_todaytext3)
         val surfaceview = view?.findViewById<SurfaceView>(R.id.surfaceview)
         val textview_starttime = view?.findViewById<TextView>(R.id.textview_starttime)
         val textview_sleeptime = view?.findViewById<TextView>(R.id.textview_sleeptime)


         //提取ID
         val PREF_FILE_NAME = "user_info"
         val User_ID = "user_id"
         val pref = activity?.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
         val user_id = pref?.getString(User_ID, "").toString()

         //绘图
         val surfaceholder = surfaceview?.holder
         val paint = Paint()

         val callback = object : SurfaceHolder.Callback {
             override fun surfaceCreated(p0: SurfaceHolder) {
                 thread {
                     val canvas = surfaceholder?.lockCanvas()
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
                     surfaceholder?.unlockCanvasAndPost(canvas)
                 }
             }

             override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

             }

             override fun surfaceDestroyed(p0: SurfaceHolder) {
             }
         }
         surfaceholder?.addCallback(callback)
         thread {
             val result_exist_people = people_service.ExistPeople(user_id)
             try {
                 val response_exist_people = result_exist_people.execute()
                 if (!response_exist_people.body()!!.string().toBoolean()) {//没这个人直接下线
                     //将下线信息记录
                     val PREF_FILE_NAME = "user_info"
                     val User_ID = "user_id"
                     val Is_Login = "is_login"

                     val pref = activity?.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
                     val editor = pref?.edit()
                     editor?.putBoolean(Is_Login, false)
                     editor?.putString(User_ID, "")
                     editor?.apply()

                     Looper.prepare()
                     Toast.makeText(activity, "请您重新登录", Toast.LENGTH_LONG).show()
                     val intent = Intent(activity, LoginActivity::class.java)
                         .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                     activity?.startActivity(intent)
                     Looper.loop()
                 }
             } catch (e: IOException) {
                 Looper.prepare()
                 Toast.makeText(activity, "网络无法连接", Toast.LENGTH_LONG).show()
                 Looper.loop()
             }
         }
         thread {
             val nowtime = Date()
             val nowtimestr =
                 (nowtime.year + 1900).toString() + "-" + (nowtime.month + 1).toString() + "-" + nowtime.date
             //获得今天的所有摄入记录
             val result_today = caffeine_service.GetTodayCaffeineRecord(user_id, nowtimestr)
             //获得上一次摄入的状态
             val result_now = caffeine_service.GetStateById(user_id)

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
                     val start_time_date = Date(start_time)
                     //上一次摄入的咖啡因
                     val start_caffeine = caffeinenow.getFloat("caffeine")
                     //上一次到目前间隔的时间
                     val delta_time = (nowtime.time - start_time) / 1000.0 / 3600.0
                     //计算出现在的咖啡因量
                     val now_caffeine = start_caffeine * Math.pow(0.5, delta_time / 4.0)
                     //计算距离睡觉量的时间差
                     val delta_sleeptime = 4 * Math.log(100.0 / start_caffeine) / Math.log(0.5)
                     //得出睡眠的时间
                     val sleeptime = Date((start_time + delta_sleeptime * 3600 * 1000+8*3600*1000).toLong())

                     //更新上方三行信息和下方信息
                     val mainHandler = Handler(Looper.getMainLooper())
                     mainHandler.post {
                         textview_todaytext1?.setText(
                             "当前体内咖啡因量：" + String.format(
                                 "%.2f",
                                 now_caffeine
                             ) + "mg"
                         )
                         textview_todaytext2?.setText("今日饮用杯数：" + caffeinelist.size + "杯")
                         textview_todaytext3?.setText(
                             "今日累计摄入：" + String.format(
                                 "%.2f",
                                 sumcaffeine
                             ) + "/400mg"
                         )

                         if (start_caffeine < 100) {//此时已经低于睡眠值了
                             textview_sleeptime?.setText("此时已经低于睡眠值了,可以入睡啦")
                         } else {
                             if(now_caffeine <100){
                                 textview_sleeptime?.setText(
                                     "此时已经低于睡眠值了,可以入睡啦\n您大约已在" + (sleeptime.year + 1900).toString() + "-" + String.format(
                                         "%02d",
                                         sleeptime.month + 1
                                     ) + "-" + String.format(
                                         "%02d",
                                         sleeptime.date
                                     ) + " " + String.format(
                                         "%02d",
                                         sleeptime.hours
                                     ) + ":" + String.format("%02d", sleeptime.minutes) + "降至睡眠量"
                                 )
                             }
                             else{
                                 textview_sleeptime?.setText(
                                     "您大约将在" + (sleeptime.year + 1900).toString() + "-" + String.format(
                                         "%02d",
                                         sleeptime.month + 1
                                     ) + "-" + String.format(
                                         "%02d",
                                         sleeptime.date
                                     ) + " " + String.format(
                                         "%02d",
                                         sleeptime.hours
                                     ) + ":" + String.format("%02d", sleeptime.minutes) + "降至睡眠量"
                                 )
                             }
                         }

                         if(now_caffeine <1){
                             textview_starttime?.setText("您很久没喝咖啡了\n您上一次饮用饮品的时间是："+(start_time_date.year + 1900).toString() + "-" + String.format(
                                 "%02d",
                                 start_time_date.month + 1
                             ) + "-" + String.format(
                                 "%02d",
                                 start_time_date.date
                             ) + " " + String.format(
                                 "%02d",
                                 start_time_date.hours
                             ) + ":" + String.format("%02d", start_time_date.minutes))
                         }
                         else{
                             textview_starttime?.setText("您上一次饮用饮品的时间是："+(start_time_date.year + 1900).toString() + "-" + String.format(
                                 "%02d",
                                 start_time_date.month + 1
                             ) + "-" + String.format(
                                 "%02d",
                                 start_time_date.date
                             ) + " " + String.format(
                                 "%02d",
                                 start_time_date.hours
                             ) + ":" + String.format("%02d", start_time_date.minutes))
                         }
                     }
                 }

             } catch (e: IOException) {
                 Looper.prepare()
                 Toast.makeText(activity, "网络无法连接", Toast.LENGTH_LONG).show()
                 Looper.loop()
             }
         }
    }
}