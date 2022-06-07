package com.shunlai.message.collect.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.message.R
import com.shunlai.message.entity.CollectBean
import com.shunlai.router.RouterManager
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_collect_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class CollectAdapter(var mContext:Context,var mDates:MutableList<CollectBean>): SRecyclerAdapter(mContext) {


    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext, R.layout.item_collect_layout,null)
        return CollectViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as CollectViewHolder
        viewHolder.setData(mDates[position])
        viewHolder.itemView.setOnClickListener {
            if (!TextUtils.isEmpty(mDates[position].ugcId)){
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.UGC_ID]= mDates[position].ugcId
                RouterManager.startActivityWithParams(BundleUrl.UGC_DETAIL_ACTIVITY,mContext as FragmentActivity,params)
            }
        }
    }

    override fun getCount(): Int=mDates.size

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<TextView>(R.id.tv_empty_value).text = "暂无点赞收藏"
        view.findViewById<ImageView>(R.id.iv_empty_image)
            .setImageResource(R.mipmap.collect_msg_empty_icon)
        return DefaultLoadMoreViewHolder(view)
    }

    inner class CollectViewHolder(var view:View):RecyclerView.ViewHolder(view){
        fun setData(bean:CollectBean){
            ImageUtil.showCircleImgWithString(view.iv_avatar,view.context,bean.memberAvatar?:"",R.mipmap.user_default_icon)
            view.iv_avatar.setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_ID]= bean.memberId
                RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,mContext as FragmentActivity,params)
            }
            view.tv_user_name.text=bean.memberName
            if (bean.type==1){
                view.tv_content.text="赞了你的笔记"
            }else{
                view.tv_content.text="收藏了你的笔记"
            }
            ImageUtil.showRoundImgWithStringAndRadius(view.iv_collect_icon,view.context,bean.image?:"",10f)
            view.tv_time.text=bean.displayTime
        }
    }

}
