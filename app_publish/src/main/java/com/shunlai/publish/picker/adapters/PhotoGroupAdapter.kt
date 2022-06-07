package com.shunlai.publish.picker.adapters

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.publish.R
import com.shunlai.publish.picker.entity.Album
import kotlinx.android.synthetic.main.item_photo_group_layout.view.*
import java.io.File

/**
 * @author Liu
 * @Date   2021/4/9
 * @mobile 18711832023
 */
class PhotoGroupAdapter(var mContext:Context,var data:MutableList<Album>,var listener:ItemClickListener):RecyclerView.Adapter<PhotoGroupAdapter.PhotoGroupHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGroupHolder {
        val view=View.inflate(mContext, R.layout.item_photo_group_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.dip2px(mContext,80f))
        return PhotoGroupHolder(view)
    }

    override fun getItemCount(): Int=data.size

    override fun onBindViewHolder(holder: PhotoGroupHolder, position: Int) {
        holder.setData(data[position])
        holder.itemView.setOnClickListener {
            listener.onItemClick(data[position])
        }
    }

    interface ItemClickListener{
        fun onItemClick(al:Album)
    }

    class PhotoGroupHolder(var view:View):RecyclerView.ViewHolder(view){
        fun setData(al:Album){
            view.tv_group_name.text=al.getDisplayName(view.context)
            view.tv_group_size.text=al.count.toString()
            ImageUtil.showRoundImgWithUriAndRadius(view.iv_group_img,view.context,
                Uri.fromFile(File(al.coverPath)),8f)

        }
    }
}
