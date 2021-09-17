package com.zhj.coffee.Center_Home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.zhj.coffee.R
import kotlinx.android.synthetic.main.activity_center.*

class CenterActivity : AppCompatActivity() {
    val fragmentList = mutableListOf<Fragment>(
        CenterFragment1(), CenterFragment2(), CenterFragment3(), CenterFragment4()
    )
    val titleList = mutableListOf<String>("今日", "咖啡记录", "分析", "我")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_center)

        val adapter = CenterTabsAdapter(this, fragmentList, titleList)
        viewPager.adapter = adapter
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }
}