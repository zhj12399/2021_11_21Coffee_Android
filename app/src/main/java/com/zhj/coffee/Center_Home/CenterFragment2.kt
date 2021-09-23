package com.zhj.coffee.Center_Home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zhj.coffee.CaffeineListAdapters.CaffeineItem
import com.zhj.coffee.CaffeineListAdapters.CaffeineListAdapter
import com.zhj.coffee.R
import com.zhj.coffee.entity.BaseUrl
import com.zhj.coffee.entity.Caffeine
import com.zhj.coffee.webservice.CaffeineService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import kotlin.concurrent.thread

class CenterFragment2 : Fragment(), CaffeineListAdapter.CaffeineListClickListener {
    lateinit var retrofit: Retrofit
    lateinit var service: CaffeineService
    var currentIndex: Int = -1
    var CaffeineList = mutableListOf<CaffeineItem>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_center2, container, false)

        //初始化网络控件
        retrofit = Retrofit.Builder().baseUrl(BaseUrl.baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(CaffeineService::class.java)

        //初始化控件
        val recyclerview = root.findViewById<RecyclerView>(R.id.recyclerview)
        val textview_emptymsg = root.findViewById<TextView>(R.id.textview_emptymsg)
        textview_emptymsg.isVisible = true

        //提取ID
        val PREF_FILE_NAME = "user_info"
        val User_ID = "user_id"
        val pref = getActivity()?.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val user_id = pref?.getString(User_ID, "").toString()

        currentIndex = -1

        thread {
            val result = service.GetCaffeineRecordById(user_id)
            try {
                val response = result.execute()
                val caffeinelist: JSONArray = JSON.parseArray(response.body()!!.string())

                for (i in 0 until caffeinelist.size) { //遍历JSONArray
                    //获得每个信息
                    val caffee = caffeinelist.getJSONObject(i)
                    val caffee_time = caffee.getDate("time")
                    val caffee_brand = caffee.getString("brand")
                    val caffee_type = caffee.getString("type")
                    val caffee_size = caffee.getString("size")
                    val caffee_percent = caffee.getFloat("percent")
                    val caffee_caffeine = caffee.getFloat("caffeine")

                    val caffee_caffee = Caffeine(
                        caffee_time,
                        caffee_brand,
                        caffee_type,
                        caffee_size,
                        caffee_percent,
                        caffee_caffeine
                    )
                    val caffeinelist_item = CaffeineItem(caffee_caffee, false)
                    CaffeineList.add(caffeinelist_item)
                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        textview_emptymsg.isVisible = false
                        recyclerview.layoutManager = LinearLayoutManager(activity)
                        recyclerview.adapter = CaffeineListAdapter(CaffeineList, this)
                    }
                }
            } catch (e: IOException) {
                Looper.prepare()
                Toast.makeText(activity, "网络无法连接", Toast.LENGTH_LONG).show()
                Looper.loop()
            }
        }
        return root
    }

    override fun onClickItem(position: Int) {
        currentIndex = position
        MaterialAlertDialogBuilder(activity!!)
            .setTitle("详情")
            .setMessage(
                CaffeineList[currentIndex].caffeine.brand + " " + CaffeineList[currentIndex].caffeine.type + " " +
                        CaffeineList[currentIndex].caffeine.size + "杯\n饮用了" +CaffeineList[currentIndex].caffeine.percent +
                        "杯\n共计咖啡因"+ CaffeineList[currentIndex].caffeine.caffeine+"mg"
            )
            .setPositiveButton("确定") { dialog, which ->
            }
            .show()
    }
}