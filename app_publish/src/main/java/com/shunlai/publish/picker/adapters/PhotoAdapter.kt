package com.shunlai.publish.picker.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.*
import com.shunlai.publish.R
import com.shunlai.publish.picker.collection.SelectCollection
import com.shunlai.publish.picker.entity.ChooseUpdateEvent
import com.shunlai.publish.picker.entity.Item
import com.shunlai.publish.picker.entity.PreviewEvent
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.item_photo_layout.view.*
import org.greenrobot.eventbus.EventBus

/**
 * @author Liu
 * @Date   2021/4/9
 * @mobile 18711832023
 */
class PhotoAdapter(var mContext:Context,var dates:MutableList<Item>,var recyclerView: RecyclerView,var limitSize:Int):RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view= View.inflate(mContext,R.layout.item_photo_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getImageResize())
        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int=dates.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.setData(dates[position],position)
        holder.itemView.setOnClickListener {
            if (dates[position].isVideo){
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.CAMERA_IMG_PATH]=dates[position].contentUri.toString()
                params[RunIntentKey.CAMERA_VIDEO_PATH]= dates[position].contentUri
                RouterManager.startActivityWithParams(BundleUrl.VIDEO_VIEW_ACTIVITY,mContext as FragmentActivity,params)
            }else{
                EventBus.getDefault().post(PreviewEvent(position))
            }

        }
    }

    fun notifyCheckStateChanged(action:Int,index:Int){
        notifyDataSetChanged()
        EventBus.getDefault().post(ChooseUpdateEvent(action,index))
    }

    override fun getItemId(position: Int): Long {
        return dates[position].id
    }

    inner class PhotoViewHolder(var view:View):RecyclerView.ViewHolder(view){
        fun setData(item:Item,position: Int){
            if (item.isVideo){
                view.tv_duration.visibility=View.VISIBLE
                view.tv_duration.text=TimeUtil.formatSecondsTo00((item.duration/1000).toInt())
            }else{
                view.tv_duration.visibility=View.INVISIBLE
            }
            ImageUtil.showCropImgWithUri(view.iv_photo,view.context,item.contentUri)
            if (SelectCollection.containsItem(item)){
                view.tv_check.setBackgroundResource(R.drawable.photo_choose_icon)
                view.transition.visibility=View.VISIBLE
                view.tv_check.text="${SelectCollection.getDataIndex(item)+1}"
            }else{
                view.tv_check.setBackgroundResource(R.mipmap.un_choose_icon)
                view.transition.visibility=View.INVISIBLE
                view.tv_check.text=""
            }
            view.ll_check.setOnClickListener {
                if (SelectCollection.containsItem(item)){
                    SelectCollection.removeItem(item)
                    notifyCheckStateChanged(1,position)
                }else{
                    if (SelectCollection.selectItem.size>=limitSize){
                        toast("最多选择${limitSize}个文件")
                        return@setOnClickListener
                    }
                    if (item.isVideo){
                        if ((item.duration/1000).toInt()>60){
                            toast("视频文件不能超过60秒")
                            return@setOnClickListener
                        }
                    }
                    SelectCollection.addItem(item)
                    notifyCheckStateChanged(2,position)
                }

            }
        }
    }

    private fun getImageResize():Int{
        val lm =recyclerView.layoutManager
        val spanCount = (lm as GridLayoutManager).spanCount
        val screenWidth=ScreenUtils.getScreenWidth(mContext)
        val availableWidth=screenWidth -mContext.resources.getDimensionPixelSize(
        R.dimen.media_grid_spacing) * (spanCount-1)
        return  availableWidth / spanCount
    }
}
