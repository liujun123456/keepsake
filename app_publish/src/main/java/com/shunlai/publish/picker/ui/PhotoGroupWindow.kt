package com.shunlai.publish.picker.ui

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.publish.R
import com.shunlai.publish.picker.adapters.PhotoGroupAdapter
import com.shunlai.publish.picker.entity.Album
import kotlinx.android.synthetic.main.window_photo_group_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/9
 * @mobile 18711832023
 */
class PhotoGroupWindow(var mContext: Context,var listener:PhotoGroupAdapter.ItemClickListener) :PopupWindow() {
    private val mAdapter by lazy {
        PhotoGroupAdapter(mContext, mutableListOf(),listener)
    }
    init {
        val view= View.inflate(mContext, R.layout.window_photo_group_layout,null)
        contentView=view
        width=ViewGroup.LayoutParams.MATCH_PARENT
        height=ScreenUtils.getScreenHeight(mContext)
        view.rv_group.layoutManager=LinearLayoutManager(mContext)
        view.rv_group.adapter=mAdapter
        update()
    }

    fun showData(targetView:View, dates:MutableList<Album>){
        showAsDropDown(targetView)
        mAdapter.data=dates
        mAdapter.notifyDataSetChanged()
    }
}
