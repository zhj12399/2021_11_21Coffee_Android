package com.zhj.coffee.Login_Register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.zhj.coffee.R

class FirstStartAppFragment(val num: Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_first_start_app, container, false)

        val textview_Center = root.findViewById<TextView>(R.id.textview_Center)
        val button_OpenApp = root.findViewById<Button>(R.id.button_OpenApp)

        when (num) {
            in 0..1 -> button_OpenApp.isVisible = false
            else -> button_OpenApp.isVisible = true
        }
        when (num) {
            0 -> textview_Center.setText("欢迎来到咖啡世界")
            1 -> textview_Center.setText("这是一款可以记录\n您日常咖啡因摄入的软件")
            2 -> textview_Center.setText("点击开启健康的咖啡生活")
        }

        button_OpenApp.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            activity?.startActivity(intent)
        }
        return root
    }
}