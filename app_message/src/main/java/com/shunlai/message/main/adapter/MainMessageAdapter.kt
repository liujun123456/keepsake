package com.shunlai.message.main.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.im.ImManager
import com.shunlai.message.R
import com.shunlai.message.entity.HomeMsgBean
import com.shunlai.message.entity.event.NotifyMsgEvent
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.item_message_layout.view.*
import org.greenrobot.eventbus.EventBus

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class MainMessageAdapter(var mContext:Context,var mdates:MutableList<HomeMsgBean>):RecyclerView.Adapter<MainMessageAdapter.MainMessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainMessageViewHolder {
        val view=View.inflate(mContext,R.layout.item_message_layout,null)
        view.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ScreenUtils.dip2px(mContext,80f))
        return MainMessageViewHolder(view)
    }

    override fun getItemCount(): Int=mdates.size

    override fun onBindViewHolder(holder: MainMessageViewHolder, position: Int) {
        holder.setData(mdates[position])
    }

    inner class MainMessageViewHolder(var view:View) :RecyclerView.ViewHolder(view){
        fun setData(bean:HomeMsgBean){
            view.tv_title.text=bean.title
            if (bean.type==0){
                view.tv_title.text="系统通知"
                view.setOnClickListener {
                    EventBus.getDefault().post(NotifyMsgEvent(1))
                    RouterManager.startActivityWithParams(BundleUrl.SYS_MSG_ACTIVITY,mContext as Activity)
                }
                ImageUtil.showCircleImgWithString(view.iv_msg_image,mContext,"",R.mipmap.system_message_icon)
            }else if (bean.type==1){
                view.tv_title.text="推送通知"
                view.setOnClickListener {
                    EventBus.getDefault().post(NotifyMsgEvent(2))
                    RouterManager.startActivityWithParams(BundleUrl.PUSH_MSG_ACTIVITY,mContext as Activity)
                }
                ImageUtil.showCircleImgWithString(view.iv_msg_image,mContext,"",R.mipmap.push_message_icon)
            }else if (bean.type==2){
                view.setOnClickListener {
                    ImManager.markerMessageAsRead(bean.userId?:"")
                    val params= mutableMapOf<String,Any?>()
                    params[RunIntentKey.TO_USER_ID]=bean.userId
                    params[RunIntentKey.TO_USER_NAME]=bean.title
                    RouterManager.startActivityWithParams(BundleUrl.CHAT_ACTIVITY,mContext as FragmentActivity,params)
                }
                ImageUtil.showCircleImgWithString(view.iv_msg_image,mContext,bean.img?:"",R.mipmap.user_default_icon)
            }
            view.tv_content.text=bean.content
            view.tv_time.text=bean.time
            if (bean.count?:0>0){
                view.tv_count.text=bean.count.toString()
                view.tv_count.visibility=View.VISIBLE
            }else{
                view.tv_count.visibility=View.GONE
            }

        }
    }
}
