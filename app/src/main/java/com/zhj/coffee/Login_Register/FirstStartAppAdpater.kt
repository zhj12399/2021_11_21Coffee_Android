package com.zhj.coffee.Login_Register

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FirstStartAppAdpater (fa:FragmentActivity) : FragmentStateAdapter(fa){

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return FirstStartAppFragment(position)
    }
}