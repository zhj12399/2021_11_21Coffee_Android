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

    private val string_brand = arrayOf("北京", "上海", "天津", "广东")
    private val string_type = arrayOf(
        arrayOf(
            "东城区", "西城区", "崇文区", "宣武区", "朝阳区", "海淀区", "丰台区", "石景山区", "门头沟区",
            "房山区", "通州区", "顺义区", "大兴区", "昌平区", "平谷区", "怀柔区", "密云县",
            "延庆县"
        ), arrayOf(
            "长宁区", "静安区", "普陀区", "闸北区", "虹口区"
        ), arrayOf(
            "和平区", "河东区", "河西区", "南开区", "河北区", "红桥区", "塘沽区", "汉沽区", "大港区"
        ), arrayOf(
            "广州", "深圳", "韶关"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_caffeine_record)

        //获得当前时间
        val nowtime = Date()
        var select_year = nowtime.year + 1900
        var select_month = nowtime.month
        var select_day = nowtime.date
        var select_hour = nowtime.hours
        var select_minute = nowtime.minutes

        //初始化控件
        textview_date.setText(select_year.toString() + "年" + (select_month + 1).toString() + "月" + select_day + "日")
        textview_time.setText(select_hour.toString() + "：" + select_minute.toString())

        val spinneradapter_brand = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, string_brand
        )
        spinner_brand.adapter = spinneradapter_brand
        spinner_brand.setSelection(0, true)

        var spinneradapter_type = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, string_type[0]
        )
        spinner_type.adapter = spinneradapter_type
        spinner_type.setSelection(0, true)

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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        button_ModifyDate.setOnClickListener {
            DatePickerDialog(
                this, { view, selectyear, selectmonth, selectday ->
                    select_year = selectyear
                    select_month = selectmonth
                    select_day = selectday
                    textview_date.setText(select_year.toString() + "年" + (select_month + 1).toString() + "月" + select_day + "日")
                }, select_year, select_month, select_day
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
    }
}

