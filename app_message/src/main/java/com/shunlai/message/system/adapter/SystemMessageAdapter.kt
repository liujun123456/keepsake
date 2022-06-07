package com.shunlai.message.system.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.message.R
import com.shunlai.message.entity.SysPushMsgBean
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_sys_msg_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class SystemMessageAdapter(var mContext:Context,var mData:MutableList<SysPushMsgBean>):SRecyclerAdapter(mContext) {



    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext,R.layout.item_sys_msg_layout,null)
        return SystemMessageViewHolder(view)
    }


    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as SystemMessageViewHolder
        viewHolder.setData(mData[position])
    }

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<TextView>(R.id.tv_empty_value).text = "暂无系统通知"
        view.findViewById<ImageView>(R.id.iv_empty_image)
            .setImageResource(R.mipmap.system_msg_empty)
        return DefaultLoadMoreViewHolder(view)
    }

    override fun getCount(): Int=mData.size


    class SystemMessageViewHolder(var view:View) :RecyclerView.ViewHolder(view){
        fun setData(bean: SysPushMsgBean){
            view.tv_msg_title.text=bean.title
            view.tv_msg_content.text=bean.content
            view.tv_msg_time.text=bean.createTime
        }

    }
}
