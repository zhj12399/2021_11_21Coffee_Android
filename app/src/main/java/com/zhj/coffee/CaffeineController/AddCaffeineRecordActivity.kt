package com.zhj.coffee.CaffeineController

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.zhj.coffee.Center_Home.CenterActivity
import com.zhj.coffee.MainActivity
import com.zhj.coffee.R
import kotlinx.android.synthetic.main.activity_add_caffeine_record.*
import java.util.*


class AddCaffeineRecordActivity : AppCompatActivity() {

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
            "大杯"
        ), arrayOf(
            "大杯"
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
        var select_time = nowtime
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

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

        button_add_record.setOnClickListener {
            select_time.year = select_year
            select_time.month = select_month
            select_time.date = select_date
            select_time.hours = select_hour
            select_time.minutes = select_minute

        }
    }
}

