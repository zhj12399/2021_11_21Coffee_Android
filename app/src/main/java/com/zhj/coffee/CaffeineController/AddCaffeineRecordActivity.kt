package com.zhj.coffee.CaffeineController

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zhj.coffee.Center_Home.CenterActivity
import com.zhj.coffee.Login_Register.LoginActivity
import com.zhj.coffee.R
import com.zhj.coffee.entity.BaseUrl
import com.zhj.coffee.entity.CaffeineBean
import com.zhj.coffee.webservice.CaffeineService
import com.zhj.coffee.webservice.PeopleService
import kotlinx.android.synthetic.main.activity_add_caffeine_record.*
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.sql.Time
import java.sql.Timestamp
import java.util.*
import kotlin.concurrent.thread


class AddCaffeineRecordActivity : AppCompatActivity() {

    //初始化网络控件
    lateinit var caffeine_retrofit: Retrofit
    lateinit var caffeine_service: CaffeineService
    lateinit var people_retrofit: Retrofit
    lateinit var people_service: PeopleService

    private val string_brand = arrayOf("星巴克", "瑞幸", "麦当劳", "雀巢", "其它")
    private val string_type = arrayOf(
        arrayOf(
            "美式", "拿铁", "摩卡", "馥芮白", "冷萃"
        ), arrayOf(
            "美式", "拿铁", "摩卡", "澳瑞白", "加浓美式"
        ), arrayOf(
            "美式", "拿铁", "摩卡", "冰醇咖啡", "卡布奇诺"
        ), arrayOf(
            "醇品速溶", "金牌速溶", "1+2速溶"
        ), arrayOf(
            "胶囊咖啡", "可乐(550ml)", "红茶(500ml)", "功能饮料(330ml)"
        )
    )
    private val string_size = arrayOf(
        arrayOf(
            "中杯", "大杯", "超大杯"
        ),
        arrayOf(
            "大杯"
        ), arrayOf(
            "大杯"
        ), arrayOf(
            "正常"
        ), arrayOf(
            "正常"
        )
    )
    private val string_percent = arrayOf(
        "1杯", "3/4杯", "2/3杯", "1/3杯", "1/4杯",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_caffeine_record)

        //获得当前时间
        val nowtime = Date()
        var select_year = nowtime.year + 1900
        var select_month = nowtime.month
        var select_date = nowtime.date
        var select_hour = nowtime.hours
        var select_minute = nowtime.minutes

        //初始化控件
        textview_date.setText(select_year.toString() + "年" + (select_month + 1).toString() + "月" + select_date + "日")
        textview_time.setText(select_hour.toString() + "：" + select_minute.toString())
        //初始化brand选择框
        val spinneradapter_brand = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, string_brand
        )
        spinner_brand.adapter = spinneradapter_brand
        spinner_brand.setSelection(0, true)
        //初始化type选择框
        var spinneradapter_type = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, string_type[0]
        )
        spinner_type.adapter = spinneradapter_type
        spinner_type.setSelection(0, true)
        //初始化size选择框
        var spinneradapter_size = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, string_size[0]
        )
        spinner_size.adapter = spinneradapter_size
        spinner_size.setSelection(1, true)
        //初始化percent选择框
        val spinneradapter_percent = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, string_percent
        )
        spinner_percent.adapter = spinneradapter_percent
        spinner_percent.setSelection(0, true)

        //初始化选择的数据
        var select_brand_num = 0
        var select_type_num = 0
        var select_size_num = 0
        var select_percent = 1.0
        var select_caffeine = 0.0

        var select_brand = string_brand[select_brand_num]
        var select_type = string_type[select_brand_num][select_type_num]
        var select_size = string_size[select_brand_num][select_size_num].dropLast(1)

        //初始化brand下拉栏监听器
        spinner_brand.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinneradapter_type = ArrayAdapter(
                    this@AddCaffeineRecordActivity,
                    android.R.layout.simple_spinner_item,
                    string_type[position]
                )
                spinner_type.adapter = spinneradapter_type

                spinneradapter_size = ArrayAdapter(
                    this@AddCaffeineRecordActivity,
                    android.R.layout.simple_spinner_item,
                    string_size[position]
                )
                spinner_size.adapter = spinneradapter_size

                //修改选择的值
                select_brand_num = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        //初始化type下拉栏监听器
        spinner_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //修改选择的值
                select_type_num = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        //初始化size下拉栏监听器
        spinner_size.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //修改选择的值
                select_size_num = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        //初始化percent下拉栏监听器
        spinner_percent.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //修改选择的值
                when (position) {
                    0 -> select_percent = 1.0
                    1 -> select_percent = 0.75
                    2 -> select_percent = 0.67
                    3 -> select_percent = 0.33
                    4 -> select_percent = 0.25
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        //初始化网络控件
        caffeine_retrofit = Retrofit.Builder().baseUrl(BaseUrl.baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        caffeine_service = caffeine_retrofit.create(CaffeineService::class.java)
        people_retrofit = Retrofit.Builder().baseUrl(BaseUrl.baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        people_service = people_retrofit.create(PeopleService::class.java)

        button_ModifyDate.setOnClickListener {
            DatePickerDialog(
                this, { view, selectyear, selectmonth, selectday ->
                    select_year = selectyear
                    select_month = selectmonth
                    select_date = selectday
                    textview_date.setText(select_year.toString() + "年" + (select_month + 1).toString() + "月" + select_date + "日")
                }, select_year, select_month, select_date
            ).show()
        }
        button_ModifyTime.setOnClickListener {
            TimePickerDialog(
                this, { view, selecthour, selectminute ->
                    select_hour = selecthour
                    select_minute = selectminute
                    textview_time.setText(select_hour.toString() + "：" + select_minute.toString())
                }, select_hour, select_minute, false
            ).show()
        }

        //提取ID
        val PREF_FILE_NAME = "user_info"
        val User_ID = "user_id"
        val pref = this.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val user_id = pref.getString(User_ID, "").toString()

        button_add_record.setOnClickListener {
            thread {
                val result_exist_people = people_service.ExistPeople(user_id)
                try {
                    val response_exist_people = result_exist_people.execute()
                    if (!response_exist_people.body()!!.string().toBoolean()) {//没这个人直接下线
                        //将下线信息记录
                        val PREF_FILE_NAME = "user_info"
                        val User_ID = "user_id"
                        val Is_Login = "is_login"

                        val pref = this.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
                        val editor = pref.edit()
                        editor.putBoolean(Is_Login, false)
                        editor.putString(User_ID, "")
                        editor.apply()

                        Toast.makeText(this, "请您重新登录", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, LoginActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        this.startActivity(intent)
                    }
                } catch (e: IOException) {
                    Toast.makeText(this, "网络无法连接", Toast.LENGTH_LONG).show()
                }
            }

            select_caffeine = when (select_brand_num) {
                0 -> {//星巴克
                    when (select_type_num) {
                        0 -> {//美式
                            when (select_size_num) {//中杯
                                0 -> 150 * select_percent
                                1 -> 225 * select_percent
                                2 -> 300 * select_percent
                                else -> 0.0
                            }
                        }
                        1 -> {//拿铁
                            when (select_size_num) {//中杯
                                0 -> 75 * select_percent
                                1 -> 150 * select_percent
                                2 -> 225 * select_percent
                                else -> 0.0
                            }
                        }
                        2 -> {//摩卡
                            when (select_size_num) {//中杯
                                0 -> 90 * select_percent
                                1 -> 175 * select_percent
                                2 -> 185 * select_percent
                                else -> 0.0
                            }
                        }
                        3 -> {//馥芮白
                            when (select_size_num) {//中杯
                                0 -> 130 * select_percent
                                1 -> 195 * select_percent
                                2 -> 260 * select_percent
                                else -> 0.0
                            }
                        }
                        4 -> {//冷萃
                            when (select_size_num) {//中杯
                                0 -> 150 * select_percent
                                1 -> 225 * select_percent
                                2 -> 300 * select_percent
                                else -> 0.0
                            }
                        }
                        else -> 0.0
                    }
                }
                1 -> {//瑞幸
                    when (select_type_num) {
                        0 -> 225 * select_percent//美式
                        1 -> 150 * select_percent//拿铁
                        2 -> 175 * select_percent//摩卡
                        3 -> 190 * select_percent//澳瑞白
                        4 -> 300 * select_percent//加浓美式
                        else -> 0.0
                    }
                }
                2 -> {//麦当劳
                    when (select_type_num) {
                        0 -> 125 * select_percent//美式
                        1 -> 125 * select_percent//拿铁
                        2 -> 140 * select_percent//摩卡
                        3 -> 225 * select_percent//冰醇咖啡
                        4 -> 125 * select_percent//卡布奇诺
                        else -> 0.0
                    }
                }
                3 -> {//雀巢
                    when (select_type_num) {
                        0 -> 70 * select_percent//醇品速溶
                        1 -> 70 * select_percent//金牌速溶
                        2 -> 50 * select_percent//1+2速溶
                        else -> 0.0
                    }
                }
                4 -> {//其它
                    when (select_type_num) {
                        0 -> 60 * select_percent//胶囊咖啡
                        1 -> 50 * select_percent//可乐
                        2 -> 105 * select_percent//红茶
                        3 -> 120 * select_percent//功能饮料
                        else -> 0.0
                    }
                }

                else -> 0.0
            }

            select_brand = string_brand[select_brand_num]
            select_type = string_type[select_brand_num][select_type_num]
            select_size = string_size[select_brand_num][select_size_num].dropLast(1)

            val select_time_timestamp =
                select_year.toString() + "-" + String.format("%02d",select_month + 1) + "-" + String.format("%02d",select_date)+ "T" + String.format("%02d",select_hour) + ":" + String.format("%02d",select_minute) + ":" + String.format("%02d",nowtime.seconds)+".000+0800"
            val caffeinebean = CaffeineBean(
                user_id, select_time_timestamp, select_brand, select_type, select_size,
                select_percent.toFloat(), select_caffeine.toFloat()
            )

            val msg =
                select_year.toString() + "." + (select_month + 1).toString() + "." + select_date.toString() + " " + select_hour.toString() + ":" + select_minute.toString() + "\n" +
                        "您选择了" + select_brand + "的一杯" + select_type + "，其杯型是：" + select_size + "杯，喝了" + select_percent.toString() + "杯，大约含有咖啡因" + String.format(
                    "%.2f",
                    select_caffeine
                ) + "mg"

            MaterialAlertDialogBuilder(this)
                .setTitle("确定是以下信息吗？")
                .setMessage(msg)
                .setNeutralButton("取消") { dialog, which ->

                }
                .setPositiveButton("是") { dialog, which ->
                    thread {
                        val result = caffeine_service.AddCaffeineRecord(caffeinebean)
                        try {
                            val response = result.execute()
                            val isAdd = response.body()!!.string().toBoolean()
                            if (isAdd) {//增添成功
                                val intent = Intent(this, CenterActivity::class.java)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                this.startActivity(intent)
                            } else {//增添失败
                                Looper.prepare()
                                Toast.makeText(this, "不能同一时间饮用两份饮品", Toast.LENGTH_LONG).show()
                                Looper.loop()
                            }
                        } catch (e: IOException) {
                            Looper.prepare()
                            Toast.makeText(this, "网络错误，提交失败", Toast.LENGTH_LONG).show()
                            Looper.loop()
                        }
                    }
                }
                .show()
        }
    }
}

