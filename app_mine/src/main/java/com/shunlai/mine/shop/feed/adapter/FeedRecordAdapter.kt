package com.shunlai.mine.shop.feed.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.FeedRecord
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter

/**
 * @author Liu
 * @Date   2021/7/22
 * @mobile 18711832023
 */
class FeedRecordAdapter(var type:Int,var mContext:Context,var mData:MutableList<FeedRecord>):SRecyclerAdapter(mContext) {
    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       if (viewType==100){
           val view=View.inflate(mContext,R.layout.item_feed_receiver_layout,null)
           return FeedRecordViewHolder(view)
       }else{
           val view=View.inflate(mContext,R.layout.item_feed_send_layout,null)
           return FeedRecordViewHolder(view)
       }
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as FeedRecordViewHolder
        viewHolder.setData(mData[position])
    }

    override fun getCount(): Int=mData.size

    override fun getViewType(position: Int): Int {
        return if (type==0){
            100
        }else{
            101
        }
    }

    class FeedRecordViewHolder(var mView:View) :RecyclerView.ViewHolder(mView){
        fun setData(bean:FeedRecord){
            mView.findViewById<TextView>(R.id.tv_title).text=bean.nickName
            ImageUtil.showCircleImgWithString(mView.findViewById(R.id.iv_avatar),mView.context,bean.avatar)
        }
    }
}
