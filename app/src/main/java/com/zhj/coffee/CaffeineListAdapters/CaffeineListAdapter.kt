package com.zhj.coffee.CaffeineListAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zhj.coffee.R

class CaffeineListAdapter(
    private val caffeinelist: List<CaffeineItem>,
    private val listener: CaffeineListClickListener?//外部数据监听器
) : RecyclerView.Adapter<CaffeineListViewHolder>() {
    //当前选中的行索引
    var selectedIndex = -1
    //外部事件响应监听器接口
    interface CaffeineListClickListener {
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaffeineListViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.caffeine_list, parent, false)
        val holder = CaffeineListViewHolder(root)
        root.setOnClickListener {
            listener?.onClickItem(holder.adapterPosition)
            setSelected(holder.adapterPosition)
        }
        return holder
    }

    //确定显示行数
    override fun getItemCount(): Int {
        return caffeinelist.size
    }
    //显示一个的内容
    override fun onBindViewHolder(holder: CaffeineListViewHolder, position: Int) {
        holder.bind(caffeinelist[position])
    }
    //供外界调用的“选中行”方法
    fun setSelected(position: Int) {
        if (position != selectedIndex && selectedIndex != -1) {
            caffeinelist[selectedIndex].isSelected = false
            notifyItemChanged(selectedIndex)
        }
        caffeinelist[position].isSelected = true
        notifyItemChanged(position)
        selectedIndex = position
    }
}