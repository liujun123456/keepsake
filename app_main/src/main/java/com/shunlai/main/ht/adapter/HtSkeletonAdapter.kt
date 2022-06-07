package com.shunlai.main.ht.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.main.R

/**
 * @author Liu
 * @Date   2021/6/24
 * @mobile 18711832023
 */
class HtSkeletonAdapter(var mContext:Context):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val img = ImageView(mContext)
        img.scaleType= ImageView.ScaleType.FIT_XY
        img.layoutParams = ViewGroup.LayoutParams(
            ScreenUtils.getScreenWidth(mContext),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return HtSkeletonViewHolder(img)
    }

    override fun getItemCount(): Int=1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as HtSkeletonViewHolder
        holder.setData()
    }

    class HtSkeletonViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(){
            (mView as ImageView).setImageResource(R.mipmap.ht_skeleton_full)
        }
    }
}
