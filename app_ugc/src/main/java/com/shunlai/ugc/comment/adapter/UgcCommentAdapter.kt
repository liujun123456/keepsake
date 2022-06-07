package com.shunlai.ugc.comment.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.ugc.R
import com.shunlai.ugc.entity.UgcCommentBean
import kotlinx.android.synthetic.main.item_list_ugc_comment_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
class UgcCommentAdapter(var mContext:Context, var mData:MutableList<UgcCommentBean>) :RecyclerView.Adapter<UgcCommentAdapter.UgcCommentViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UgcCommentViewHolder {
       val view:View=View.inflate(mContext, R.layout.item_list_ugc_comment_layout,null)
        return UgcCommentViewHolder(view)
    }

    override fun getItemCount(): Int=mData.size

    override fun onBindViewHolder(holder: UgcCommentViewHolder, position: Int) {
        holder.setData(mData[position])
    }

    class UgcCommentViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean:UgcCommentBean){
            ImageUtil.showRoundImgWithStringAndRadius(mView.iv_comment_img,mView.context,bean.images?:"",16f,
                leftTop = true,
                rightTop = true,
                leftBottom = false,
                rightBottom = false
            )
            ImageUtil.showCircleImgWithString(mView.iv_user_avatar,mView.context,bean.avatar?:"")
            mView.tv_comment_title.text=bean.title
            mView.sl_layout.setRating(bean.evaluate?:0)
            mView.tv_user_name.text=bean.nickName
            if (bean.likes==0){
                mView.tv_fabulous_count.text=""
            }else{
                mView.tv_fabulous_count.text=bean.likes.toString()
            }
        }
    }
}
