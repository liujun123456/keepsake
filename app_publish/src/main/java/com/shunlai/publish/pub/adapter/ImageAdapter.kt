package com.shunlai.publish.pub.adapter

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.bean.PathItem
import com.shunlai.common.utils.*
import com.shunlai.publish.Constant
import com.shunlai.publish.R
import com.shunlai.router.RouterManager
import com.shunlai.ui.moveRv.MoveAdapter
import kotlinx.android.synthetic.main.item_photo_2_layout.view.*
import java.util.*
import kotlin.reflect.typeOf

/**
 * @author Liu
 * @Date   2021/4/12
 * @mobile 18711832023
 */
class ImageAdapter(var mContext:Context, var mData:MutableList<PathItem>,
                   var recyclerView: RecyclerView,
                   var onHandleViewListener:OnHandleViewListener):MoveAdapter() {

    private val TYPE_SHOW=100
    private val TYPE_ADD=101
    var showType=1  //1图片 2视频

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==TYPE_SHOW){
            val view:View=View.inflate(mContext, R.layout.item_photo_2_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getImageResize())
            return ImageViewHolder(view)
        }else{
            val view:View=View.inflate(mContext, R.layout.item_new_add_photo_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getImageResize())
            return AddImageViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        if (showType==1){
            return if (mData.size>=9) 9 else mData.size+1
        }else{
            return mData.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder){
            holder.setData(mData[position],position)
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(mData,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
    }

    override fun getItemViewType(position: Int): Int {
        if (mData.size==9){
            return TYPE_SHOW
        }else{
            return if (position>=mData.size){
                TYPE_ADD
            }else{
                TYPE_SHOW
            }
        }
    }

    private fun getImageResize():Int{
        val lm =recyclerView.layoutManager
        val spanCount = (lm as GridLayoutManager).spanCount
        val screenWidth= ScreenUtils.getScreenWidth(mContext)
        val availableWidth=screenWidth -ScreenUtils.dip2px(mContext,16f) * (spanCount-1)-ScreenUtils.dip2px(mContext,36f)
        return  availableWidth / spanCount
    }


    interface OnHandleViewListener{
        fun removeItem(item:PathItem,position: Int)

        fun onTakePhoto()
    }


    inner class ImageViewHolder(var view:View):RecyclerView.ViewHolder(view){
        fun setData(item:PathItem,position:Int){
            ImageUtil.showRoundImgWithUriAndRadius(view.iv_photo,view.context, Uri.parse(item.path),16f)
            view.ll_delete.setOnClickListener {
                onHandleViewListener.removeItem(item,position)
            }
            view.setOnClickListener {
                if (item.type==2){
                    val params = mutableMapOf<String, Any?>()
                    params[RunIntentKey.CAMERA_IMG_PATH] = item.path
                    params[RunIntentKey.CAMERA_VIDEO_PATH] = Uri.parse(item.path)
                    RouterManager.startActivityWithParams(BundleUrl.VIDEO_VIEW_ACTIVITY, mContext as FragmentActivity, params)
                }else{
                    val params= mutableMapOf<String,Any?>()
                    params[RunIntentKey.CHOOSE_IMAGE_ITEM]= mData
                    params[RunIntentKey.CHOOSE_IMAGE_INDEX]=position
                    RouterManager.startActivityWithParams(BundleUrl.PHOTO_PREVIEW_PATH_ACTIVITY,mContext as Activity,params)

                }
             }

            if (item.type==2){
                view.iv_play_video.visibility=View.VISIBLE
                view.tv_video_long.visibility=View.VISIBLE
                view.tv_video_long.text= TimeUtil.formatSecondsTo00(item.duration.div(1000).toInt())
            }else{
                view.tv_video_long.visibility=View.GONE
                view.iv_play_video.visibility=View.GONE
            }
        }
    }

    inner class AddImageViewHolder(var view:View):RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener {
                onHandleViewListener.onTakePhoto()
            }
        }

    }
}
