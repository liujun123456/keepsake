package com.shunlai.im.adapter

import android.content.Context
import android.view.ViewGroup
import com.shunlai.im.adapter.viewHolder.BaseViewHolder
import com.tencent.imsdk.v2.V2TIMMessage

/**
 * @author Liu
 * @Date   2021/4/22
 * @mobile 18711832023
 */
class ChatAdapter(var mCtx:Context,var mData:MutableList<V2TIMMessage>):BaseChatAdapter(mCtx){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return buildViewHolder(viewType)
    }

    override fun getItemCount(): Int=mData.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.setData(mData[position],position)
    }

    override fun getItemViewType(position: Int): Int {
        return buildViewType(mData[position])
    }

    override fun getItem(position: Int): V2TIMMessage {
        return mData[position]
    }

}
