package com.shunlai.mine.shop.impression.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.ImpressionBean
import com.shunlai.mine.shop.impression.ShopImpressionListener
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import kotlinx.android.synthetic.main.item_send_impression_layout.view.*

/**
 * @author Liu
 * @Date   2021/7/8
 * @mobile 18711832023
 */
class SendImpressionAdapter(var mContext: Context,var mData:MutableList<ImpressionBean>,var mListener: ShopImpressionListener): SRecyclerAdapter(mContext) {
    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext,R.layout.item_send_impression_layout,null)
        return SendImpressionViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as SendImpressionViewHolder
        viewHolder.setData(mData[position])
        viewHolder.itemView.setOnLongClickListener {
            mListener.onDelete(position)
            return@setOnLongClickListener false
        }
    }

    override fun getCount(): Int=mData.size

    class SendImpressionViewHolder(var mView: View):RecyclerView.ViewHolder(mView){
        fun setData(bean: ImpressionBean){
            ImageUtil.showCircleImgWithString(mView.iv_avatar,mView.context,bean.avatar)
            mView.tv_name.text=bean.nickName
            mView.tv_content.text=bean.content
        }

    }

}
