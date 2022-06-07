package com.shunlai.mine.account.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.BlackUserBean
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_black_user_layout.view.*

/**
 * @author Liu
 * @Date   2021/8/25
 * @mobile 18711832023
 */
class BlackListAdapter(var mContext:Context,var mData:MutableList<BlackUserBean>,var mListener:BlackListener):SRecyclerAdapter(mContext){
    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext,R.layout.item_black_user_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ScreenUtils.dip2px(mContext,80f))
        return BlackViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as BlackViewHolder
        viewHolder.setData(mData[position],position)
    }

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<TextView>(R.id.tv_empty_value).text="黑名单空空如也"
        return DefaultLoadMoreViewHolder(view)
    }

    override fun getCount(): Int=mData.size

    inner class BlackViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean:BlackUserBean,position: Int){
            ImageUtil.showCircleImgWithString(mView.iv_user_avatar,mView.context,bean.avatar?:"",R.mipmap.user_default_icon)
            mView.tv_name.text=bean.nickName
            mView.tv_desc.text=bean.introduce
            mView.tv_attention.setOnClickListener {
                mListener.cancelBlack(bean.memberId?:"",position)
            }
        }
    }

    interface BlackListener{
        fun cancelBlack(memberId:String,position: Int)
    }
}
