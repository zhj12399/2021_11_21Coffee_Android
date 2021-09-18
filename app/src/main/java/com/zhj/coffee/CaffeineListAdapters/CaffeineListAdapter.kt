package com.zhj.coffee.CaffeineListAdapters

import androidx.recyclerview.widget.RecyclerView

class CaffeineListAdapter(
    private val caffeinelist: List<CaffeineItem>,
    private val listener: CaffeineListClickListener?
) : RecyclerView.Adapter<CaffeineListViewHolder>() {
    //当前选中的行索引
    var selectedIndex = -1

    interface CaffeineListClickListener {
        fun onClickItem(position: Int)
    }
}