package com.shunlai.mine.fragment.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.WallPhotoBean
import kotlinx.android.synthetic.main.item_wall_img_show.view.*

/**
 * @author Liu
 * @Date   2021/5/18
 * @mobile 18711832023
 * type 0 数据无限循环 1展示真实数据量
 */
class ImgWallAdapter(var mContext: Context, var mData: MutableList<WallPhotoBean>, var type: Int=0) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 10086) {
            val view = View.inflate(mContext, R.layout.item_wall_img_defalut, null)
            return DefaultWallViewHolder(view)
        } else {
            val view = View.inflate(mContext, R.layout.item_wall_img_show, null)
            return PhotoWallViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        if (type == 0) {
            return Int.MAX_VALUE
        } else {
            return mData.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DefaultWallViewHolder) {
            holder.setData(position)
        } else {
            holder as PhotoWallViewHolder
            holder.setData(mData[position.rem(mData.size)], position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (mData.isNullOrEmpty()) {
            return 10086
        } else {
            return 10087
        }
    }


    inner class DefaultWallViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun setData(position: Int) {
            if (position.rem(5) < 2) {
                view.layoutParams = ViewGroup.LayoutParams(ScreenUtils.getScreenWidth(mContext).div(2), ScreenUtils.getScreenWidth(mContext).div(2))
            } else {
                view.layoutParams = ViewGroup.LayoutParams(ScreenUtils.getScreenWidth(mContext).div(3), ScreenUtils.getScreenWidth(mContext).div(3))
            }
        }
    }

    inner class PhotoWallViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        fun setData(bean: WallPhotoBean, position: Int) {
            if (position.rem(5) < 2) {
                view.layoutParams = ViewGroup.LayoutParams(ScreenUtils.getScreenWidth(mContext).div(2), ScreenUtils.getScreenWidth(mContext).div(2))
            } else {
                view.layoutParams = ViewGroup.LayoutParams(ScreenUtils.getScreenWidth(mContext).div(3), ScreenUtils.getScreenWidth(mContext).div(3))
            }
            ImageUtil.showCropImgWithString(view.tv_img_show, mContext, bean.imageUrl ?: "",R.mipmap.wall_default_icon)
        }
    }
}

