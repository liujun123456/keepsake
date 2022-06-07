package com.shunlai.im.adapter.viewHolder

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.shunlai.im.R
import com.shunlai.im.adapter.BaseChatAdapter
import com.shunlai.im.utils.Constant
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_MSG_STATUS_SEND_FAIL
import kotlinx.android.synthetic.main.item_chat_txt_right_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/22
 * @mobile 18711832023
 */
class TextViewProvider(var type:Int, var mContext: Context,var mAdapter: BaseChatAdapter){
    fun getViewHolder(): BaseViewHolder{
        return if (type== Constant.TXT_RECEIVE){
            val view=View.inflate(mContext, R.layout.item_chat_txt_left_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            TxtLeftHolder(view)
        }else{
            val view=View.inflate(mContext, R.layout.item_chat_txt_right_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            TxtRightHolder(view)
        }
    }


    inner class TxtLeftHolder(view:View): BaseChatViewHolder(view){
        override fun setData(msg: Any, position: Int) {
            try {
                msg as V2TIMMessage
                setTimestamp(msg,position,mAdapter)
                setContentValue(msg.textElem.text)
                loadAvatar(msg.faceUrl,msg.sender?:"")
            }catch (e:Exception){

            }
        }
    }

    inner class TxtRightHolder(view:View): BaseChatViewHolder(view){
        override fun setData(msg: Any, position: Int) {
            try {
                msg as V2TIMMessage
                setTimestamp(msg,position,mAdapter)
                setContentValue(msg.textElem.text)
                loadAvatar(msg.faceUrl,msg.sender?:"")

                if (msg.status== V2TIM_MSG_STATUS_SEND_FAIL){
                    view.iv_send_fail.visibility=View.VISIBLE
                }else{
                    view.iv_send_fail.visibility=View.INVISIBLE
                }
            }catch (e:Exception){

            }
        }
    }
}
