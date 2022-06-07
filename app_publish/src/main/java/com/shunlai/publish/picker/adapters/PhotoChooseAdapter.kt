package com.shunlai.publish.picker.adapters

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.bean.PathItem
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.publish.R
import com.shunlai.publish.picker.collection.SelectCollection
import com.shunlai.publish.picker.entity.ChooseUpdateEvent
import com.shunlai.publish.picker.entity.Item
import com.shunlai.publish.picker.entity.MoveUpdateEvent
import com.shunlai.ui.moveRv.MoveAdapter
import kotlinx.android.synthetic.main.item_photo_choose_layout.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * @author Liu
 * @Date   2021/4/9
 * @mobile 18711832023
 */
class PhotoChooseAdapter(var mContext:Context):MoveAdapter(){

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseViewHolder {
        val view= View.inflate(mContext, R.layout.item_photo_choose_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ScreenUtils.dip2px(mContext,64f),ScreenUtils.dip2px(mContext,64f))
        return ChooseViewHolder(view)
    }

    override fun getItemCount(): Int= SelectCollection.selectItem.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ChooseViewHolder
        holder.setData(SelectCollection.selectItem[position],position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(SelectCollection.selectItem,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
        EventBus.getDefault().post(MoveUpdateEvent())
    }

    override fun getItemId(position: Int): Long {
        return SelectCollection.selectItem[position].id
    }


    inner class ChooseViewHolder(var view:View):RecyclerView.ViewHolder(view){
        fun setData(item: PathItem, position: Int){
            ImageUtil.showRoundImgWithStringAndRadius(view.iv_choose_img,view.context, item.path,8f)
            view.rl_delete_img.setOnClickListener {
                SelectCollection.selectItem.remove(item)
                EventBus.getDefault().post(MoveUpdateEvent())
                EventBus.getDefault().post(ChooseUpdateEvent(1,position))
            }
        }
    }
}
