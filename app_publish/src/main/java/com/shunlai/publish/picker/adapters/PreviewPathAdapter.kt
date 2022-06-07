package com.shunlai.publish.picker.adapters

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.bean.PathItem
import com.shunlai.common.utils.ImageUtil
import com.shunlai.publish.R
import com.shunlai.publish.picker.entity.Item
import kotlinx.android.synthetic.main.item_preview_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/15
 * @mobile 18711832023
 */
class PreviewPathAdapter(var mCtx:Context, var mDates:MutableList<PathItem>):RecyclerView.Adapter<PreviewPathAdapter.PreviewViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewViewHolder {
        val view=View.inflate(mCtx, R.layout.item_preview_layout,null)
        view.layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        return PreviewViewHolder(view)
    }

    override fun getItemCount(): Int=mDates.size

    override fun onBindViewHolder(holder: PreviewViewHolder, position: Int) {
        holder.setData(mDates[position])
    }

    class PreviewViewHolder(var view:View):RecyclerView.ViewHolder(view){
        fun setData(item:PathItem){
            ImageUtil.showPreView(view.iv_preview,view.context, Uri.parse(item.path))
        }
    }
}
