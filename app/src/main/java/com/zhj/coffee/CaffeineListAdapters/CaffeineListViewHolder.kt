package com.zhj.coffee.CaffeineListAdapters

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zhj.coffee.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class CaffeineListViewHolder(caffeineListItemView: View) :
    RecyclerView.ViewHolder(caffeineListItemView) {

    private val textview_title = caffeineListItemView.findViewById<TextView>(R.id.textview_title)
    private val textview_message =
        caffeineListItemView.findViewById<TextView>(R.id.textview_message)

    fun bind(caffeineItem: CaffeineItem) {
        textview_title.setText(SimpleDateFormat("yyyy-MM-dd HH:mm").format(caffeineItem.caffeine.time)
                + " 饮用了 " + caffeineItem.caffeine.type)

        textview_message.setText(caffeineItem.caffeine.brand+" "+caffeineItem.caffeine.size + ",咖啡因含量:" +caffeineItem.caffeine.caffeine+"mg")
        if(caffeineItem.isSelected){
            itemView.setBackgroundColor(Color.CYAN)
        } else {
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}
