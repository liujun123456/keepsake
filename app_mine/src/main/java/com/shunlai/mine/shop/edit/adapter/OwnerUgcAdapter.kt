package com.shunlai.mine.shop.edit.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.OwnerUgcBean
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import kotlinx.android.synthetic.main.owner_item_ugc_layout.view.*

/**
 * @author Liu
 * @Date   2021/7/22
 * @mobile 18711832023
 */
class OwnerUgcAdapter(var mContext: Context, var mData:MutableList<OwnerUgcBean>,var mListener:OwnerUgcListener):SRecyclerAdapter(mContext)  {
    var choosePosition=-1

    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext, R.layout.owner_item_ugc_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        return OwnerUgcViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as OwnerUgcViewHolder
        viewHolder.setData(mData[position],position)
        viewHolder.itemView.setOnClickListener {
            choosePosition=position
            notifyDataSetChanged()
            mListener.onOwnerUgcClick(mData[position])
        }
    }

    override fun getCount(): Int=mData.size


    inner class OwnerUgcViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean:OwnerUgcBean,position: Int){
            mView.tv_ugc_content.text=bean.content
            mView.tv_user_name.text=bean.nickName
            mView.ugc_img.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,getRealImgSize())
            ImageUtil.showRoundImgWithStringAndRadius(mView.ugc_img,mView.context,bean.firstImage?:"",16f)
            ImageUtil.showCircleImgWithString(mView.iv_user_avatar,mView.context,bean.avatar?:"",R.mipmap.user_default_icon)
            if (TextUtils.isEmpty(bean.likes)||bean.likes=="0"){
                mView.tv_fabulous_count.text=""
                mView.iv_fabulous_label.visibility=View.INVISIBLE
            }else{
                mView.tv_fabulous_count.text=bean.likes
                mView.iv_fabulous_label.visibility=View.VISIBLE
            }
            if (position==choosePosition){
                mView.ll_content.setBackgroundResource(R.drawable.owner_ugc_select_bg)
            }else{
                mView.ll_content.setBackgroundResource(0)
            }
        }

        private fun getRealImgSize():Int{
            val userWidth=ScreenUtils.getScreenWidth(mContext)- ScreenUtils.dip2px(mContext,48f)
            return userWidth.div(2)
        }
    }

    interface OwnerUgcListener{
        fun onOwnerUgcClick(bean:OwnerUgcBean)
    }

}
