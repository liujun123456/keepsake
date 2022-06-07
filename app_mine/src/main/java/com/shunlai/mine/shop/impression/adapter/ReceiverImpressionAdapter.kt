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
import kotlinx.android.synthetic.main.item_receiver_impression_layout.view.*

/**
 * @author Liu
 * @Date   2021/7/8
 * @mobile 18711832023
 */
class ReceiverImpressionAdapter(var mContext:Context,var mData:MutableList<ImpressionBean>,var mListener: ShopImpressionListener):SRecyclerAdapter(mContext) {
    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext,R.layout.item_receiver_impression_layout,null)
        return ReceiverImpressionViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as ReceiverImpressionViewHolder
        viewHolder.setData(mData[position],position)
        viewHolder.itemView.setOnLongClickListener {
            mListener.onDelete(position)
            return@setOnLongClickListener false
        }
    }

    override fun getCount(): Int=mData.size

    inner class ReceiverImpressionViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean:ImpressionBean,position: Int){
            ImageUtil.showCircleImgWithString(mView.iv_avatar,mView.context,bean.avatar)
            mView.tv_name.text=bean.nickName
            if (bean.chosedFlag==0){
                mView.tv_choose_state.text="精选"
                mView.tv_choose_state.setBackgroundResource(R.drawable.alpha_0d_radius16_bg)
            }else{
                mView.tv_choose_state.text="取消精选"
                mView.tv_choose_state.setBackgroundResource(R.drawable.gray_empty_radius_20)
            }
            mView.tv_choose_state.setOnClickListener {
                mListener.onChoose(position)
            }
            mView.tv_content.text=bean.content
        }
    }
}
