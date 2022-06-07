package com.shunlai.message.attention.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.message.R
import com.shunlai.message.entity.AttentionBean
import com.shunlai.message.entity.event.AttentionEvent
import com.shunlai.router.RouterManager
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_attention_layout.view.*
import org.jetbrains.anko.textColor

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class AttentionAdapter(var mContext:Context,var mDates:MutableList<AttentionBean>,var mListener:AttentionClickListener): SRecyclerAdapter(mContext) {


    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext, R.layout.item_attention_layout,null)
        return AttentionViewHolder(view)
    }


    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as AttentionViewHolder
        viewHolder.setData(mDates[position],position)
    }

    override fun getCount(): Int=mDates.size

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<TextView>(R.id.tv_empty_value).text = "暂无新增关注"
        view.findViewById<ImageView>(R.id.iv_empty_image)
            .setImageResource(R.mipmap.new_attention_empty_icon)
        return DefaultLoadMoreViewHolder(view)
    }


    inner class AttentionViewHolder(var view:View):RecyclerView.ViewHolder(view){
        fun setData(bean: AttentionBean,position: Int){
            ImageUtil.showCircleImgWithString(view.iv_avatar,view.context,bean.memberAvatar?:"",R.mipmap.user_default_icon)
            view.iv_avatar.setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_ID]= bean.memberId
                RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,mContext as FragmentActivity,params)
            }
            view.tv_user_name.text=bean.memberName
            view.tv_content.text=bean.introduce
            if (bean.isEachOther==1){
                view.tv_attention.setBackgroundResource(R.drawable.gray_radius_16_no_solid_bg)
                view.tv_attention.textColor= Color.parseColor("#191919")
                view.tv_attention.text="已关注"
            }else{
                view.tv_attention.setBackgroundResource(R.drawable.black_radius_24_bg)
                view.tv_attention.textColor=ContextCompat.getColor(view.context,R.color.message_white)
                view.tv_attention.text="回关"
            }
            view.tv_attention.setOnClickListener {
                mListener.onAttentionClick(AttentionEvent(bean.memberId?:"",position))
            }
        }
    }

    interface AttentionClickListener{
        fun onAttentionClick(event: AttentionEvent)
    }


}
