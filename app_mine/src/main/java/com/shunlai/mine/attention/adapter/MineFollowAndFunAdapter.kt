package com.shunlai.mine.attention.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.AttentionEvent
import com.shunlai.mine.entity.bean.FollowAndFun
import com.shunlai.router.RouterManager
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_mine_follow_fun_item.view.*
import org.jetbrains.anko.textColor

/**
 * @author Liu
 * @Date   2021/5/25
 * @mobile 18711832023
 */
class MineFollowAndFunAdapter(var mContext:Context,var mData:MutableList<FollowAndFun>,var mListener:AttentionClickListener,var type:Int):SRecyclerAdapter(mContext) {
    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       val view=View.inflate(mContext,R.layout.item_mine_follow_fun_item,null)
        return MineFollowAndFunViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as MineFollowAndFunViewHolder
        viewHolder.setData(mData[position],position)
    }

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        if (type==0){
            view.findViewById<TextView>(R.id.tv_empty_value).text="你还没有关注的人哦！"
        }else{
            view.findViewById<TextView>(R.id.tv_empty_value).text="你还没有粉丝哦！"
        }
        return DefaultLoadMoreViewHolder(view)
    }

    override fun getCount(): Int=mData.size

    inner class MineFollowAndFunViewHolder(var mView:View):RecyclerView.ViewHolder(mView){
        fun setData(bean:FollowAndFun,position: Int){
            ImageUtil.showCircleImgWithString(mView.iv_avatar,mView.context,bean.avatar?:"",R.mipmap.user_default_icon)
            mView.iv_avatar.setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_ID]= bean.id.toString()
                RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,mContext as FragmentActivity,params)
            }
            mView.tv_user_name.text=bean.nickName
            mView.tv_content.text=bean.introduce
            if (type==0){
                if (bean.isFollow==1){
                    mView.tv_attention.setBackgroundResource(R.drawable.gray_empty_radius_2)
                    mView.tv_attention.textColor= Color.parseColor("#B5B5B5")
                    mView.tv_attention.text="取消关注"
                }else{
                    mView.tv_attention.setBackgroundResource(R.drawable.black_radius_2_bg)
                    mView.tv_attention.textColor= Color.parseColor("#FFFFFF")
                    mView.tv_attention.text="关注"
                }
            }else{
                if (bean.isEachOther==1){
                    mView.tv_attention.setBackgroundResource(R.drawable.gray_empty_radius_2)
                    mView.tv_attention.textColor= Color.parseColor("#B5B5B5")
                    mView.tv_attention.text="取消关注"
                }else{
                    mView.tv_attention.setBackgroundResource(R.drawable.black_radius_2_bg)
                    mView.tv_attention.textColor= Color.parseColor("#FFFFFF")
                    mView.tv_attention.text="回关"
                }
            }
            mView.tv_attention.setOnClickListener {
                mListener.onAttentionClick(AttentionEvent(bean.id?:0,position))
            }
        }
    }

    interface AttentionClickListener{
        fun onAttentionClick(event: AttentionEvent)
    }
}
