package com.shunlai.message.push.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.ImageUtil
import com.shunlai.message.R
import com.shunlai.message.entity.SysPushMsgBean
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder
import com.shunlai.ui.srecyclerview.views.FooterView
import kotlinx.android.synthetic.main.item_push_msg_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class PushMessageAdapter(var mContext:Context,var mData:MutableList<SysPushMsgBean>): SRecyclerAdapter(mContext) {


    override fun onCreateHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=View.inflate(mContext, R.layout.item_push_msg_layout,null)
        return PushMessageViewHolder(view)
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewHolder as PushMessageViewHolder
        viewHolder.setData(mData[position])
    }

    override fun getCount(): Int=mData.size

    override fun getLoadMoreViewHolder(): LoadMoreViewHolder {
        val view = FooterView(mContext)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        view.findViewById<TextView>(R.id.tv_empty_value).text = "暂无推送消息"
        view.findViewById<ImageView>(R.id.iv_empty_image)
            .setImageResource(R.mipmap.push_message_empty_icon)
        return DefaultLoadMoreViewHolder(view)
    }


    class PushMessageViewHolder(var view:View) :RecyclerView.ViewHolder(view){
        fun setData(bean: SysPushMsgBean){
            view.tv_msg_content.text=bean.content
            view.tv_msg_time.text=bean.createTime
            if (TextUtils.isEmpty(bean.image)){
                view.iv_msg_image.visibility=View.GONE
            }else{
                view.iv_msg_image.visibility=View.VISIBLE
                ImageUtil.showCropImgWithString(view.iv_msg_image,view.context,bean.image?:"")
            }
        }
    }
}
