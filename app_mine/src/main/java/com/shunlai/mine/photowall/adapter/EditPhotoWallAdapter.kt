package com.shunlai.mine.photowall.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.WallPhotoBean
import kotlinx.android.synthetic.main.item_photo_wall_edit.view.*

/**
 * @author Liu
 * @Date   2021/5/26
 * @mobile 18711832023
 */
class EditPhotoWallAdapter(var mContext:Context,var mData:MutableList<WallPhotoBean>,var recyclerView: RecyclerView,var chooseData:MutableList<WallPhotoBean>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        setHasStableIds(true)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view= View.inflate(mContext, R.layout.item_photo_wall_edit,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getImageResize())
        return EditPhotoWallViewHolder(view)
    }

    override fun getItemCount(): Int=mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as EditPhotoWallViewHolder
        holder.setData(mData[position],position)

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class EditPhotoWallViewHolder(var view:View):RecyclerView.ViewHolder(view){
        fun setData(bean:WallPhotoBean,position: Int){
            ImageUtil.showCropImgWithString(view.iv_photo,view.context,bean.imageUrl?:"")
            view.tv_check.setBackgroundResource(R.mipmap.photo_un_choose_icon)
            chooseData.forEach {
                if (it.imageKey==bean.imageKey){
                    view.tv_check.setBackgroundResource(R.mipmap.wall_photo_choose_icon)
                    return@forEach
                }
            }
            view.ll_check.setOnClickListener {
                var index=-1
                chooseData.forEachIndexed { i, wallPhotoBean ->
                    if (wallPhotoBean.imageKey==bean.imageKey){
                        index=i
                        return@forEachIndexed
                    }
                }
                if (index==-1){
                    chooseData.add(bean)
                }else{
                    chooseData.removeAt(index)
                }
                notifyItemChanged(position)
            }
        }
    }


    private fun getImageResize():Int{
        val lm =recyclerView.layoutManager
        val spanCount = (lm as GridLayoutManager).spanCount
        val screenWidth= ScreenUtils.getScreenWidth(mContext)
        val availableWidth=screenWidth -ScreenUtils.dip2px(mContext,4f) * (spanCount-1)
        return  availableWidth / spanCount
    }
}
