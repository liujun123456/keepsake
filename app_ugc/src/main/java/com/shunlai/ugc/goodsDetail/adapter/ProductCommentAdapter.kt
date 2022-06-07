package com.shunlai.ugc.goodsDetail.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.ugc.R
import com.shunlai.ugc.entity.UgcCommentBean
import kotlinx.android.synthetic.main.item_ugc_comment_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
class ProductCommentAdapter(var mContext:Context,var mData:MutableList<UgcCommentBean>) :RecyclerView.Adapter<ProductCommentAdapter.ProductCommentViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCommentViewHolder {
       val view:View=View.inflate(mContext, R.layout.item_ugc_comment_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ScreenUtils.dip2px(mContext,155f),ViewGroup.LayoutParams.WRAP_CONTENT)
        return ProductCommentViewHolder(view)
    }

    override fun getItemCount(): Int=mData.size

    override fun onBindViewHolder(holder: ProductCommentViewHolder, position: Int) {
        holder.setData(mData[position])
    }

    class ProductCommentViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean:UgcCommentBean){
            ImageUtil.showRoundImgWithStringAndRadius(mView.iv_comment_img,mView.context,bean.images?:"",16f)
            ImageUtil.showCircleImgWithString(mView.iv_user_avatar,mView.context,bean.avatar?:"")
            mView.tv_comment_title.text=bean.title
            mView.sl_layout.setRating(bean.evaluate?:0)
            mView.tv_user_name.text=bean.nickName
            if (bean.likes==0){
                mView.tv_fabulous_count.text=""
                mView.iv_fabulous_label.visibility=View.INVISIBLE
            }else{
                mView.tv_fabulous_count.text=bean.likes.toString()
                mView.iv_fabulous_label.visibility=View.VISIBLE
            }

        }
    }
}
