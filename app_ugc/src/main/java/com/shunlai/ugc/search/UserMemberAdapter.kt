package com.shunlai.ugc.search

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.router.RouterManager
import com.shunlai.ugc.R
import com.shunlai.ugc.entity.SearchUserBean
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_search_user_layout.view.*

/**
 * @author Liu
 * @Date   2021/8/24
 * @mobile 18711832023
 */
class UserMemberAdapter(var mData:MutableList<SearchUserBean>,var mContext:Context,var mListener:AttentionListener):SRecyclerAdapter(mContext) {
    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext,R.layout.item_search_user_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.dip2px(mContext,80f))
        return UserMemberViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as UserMemberViewHolder
        viewHolder.setData(position,mData[position])
        viewHolder.mView.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.MEMBER_ID]= mData[position].memberId
            RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,mContext as FragmentActivity,params)
        }
    }

    override fun getCount(): Int=mData.size

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<TextView>(R.id.tv_empty_value).text="搜索结果为空"
        view.findViewById<ImageView>(R.id.iv_empty_image).setImageResource(R.mipmap.empty_search_image)
        return DefaultLoadMoreViewHolder(view)
    }

    inner class UserMemberViewHolder(var mView: View):RecyclerView.ViewHolder(mView){

        fun setData(position: Int,bean:SearchUserBean){
            ImageUtil.showCircleImgWithString(mView.iv_user_avatar,mView.context,bean.avatar?:"",R.mipmap.user_default_icon)
            mView.tv_name.text=bean.nickName
            mView.tv_desc.text=bean.introduce

            if (bean.isFollow==1){
                mView.tv_attention.text="已关注"
                mView.tv_attention.setBackgroundResource(R.drawable.have_attention_bg)

            }else{
                mView.tv_attention.text="+ 关注"
                mView.tv_attention.setBackgroundResource(R.drawable.action_attention_bg)

            }
            mView.tv_attention.setOnClickListener {
                mListener.doAttention(position,bean.memberId?:"",bean.isFollow?:0)
            }
        }
    }

    interface AttentionListener{
        fun doAttention(position:Int,memberId:String,isFollow:Int)
    }
}
