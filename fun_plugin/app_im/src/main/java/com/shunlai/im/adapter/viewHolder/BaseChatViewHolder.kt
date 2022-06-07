package com.shunlai.im.adapter.viewHolder

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.im.R
import com.shunlai.im.adapter.BaseChatAdapter
import com.shunlai.im.face.FaceManager
import com.shunlai.im.utils.DateTimeUtil
import com.shunlai.router.RouterManager
import com.tencent.imsdk.v2.V2TIMMessage
import java.io.File
import java.util.*

/**
 * @author Liu
 * @Date   2021/4/22
 * @mobile 18711832023
 */
abstract class BaseChatViewHolder(var view: View) :BaseViewHolder(view) {

    fun setContentValue(data: String) {
        FaceManager.handlerEmojiText(view.findViewById(R.id.tv_content),data,false)
    }

    fun loadAvatar(url:String,senderId:String){
        ImageUtil.showCircleImgWithString(view.findViewById(R.id.iv_avatar),view.context,url,
           R.mipmap.user_default_icon)
        view.findViewById<ImageView>(R.id.iv_avatar).setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.MEMBER_ID]= senderId
            RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,view.context as FragmentActivity,params)
        }
    }

    fun loadImg(url: String?){
        ImageUtil.showRoundImgWithUri(view.findViewById(R.id.iv_content),view.context,Uri.parse(url?:""))
//        ImageUtil.showRoundImgWithStringAndRadius(view.findViewById(R.id.iv_content),view.context,url?:"",3f)
    }

    fun loadLocalImg(path:String){
        ImageUtil.showRoundImgWithUri(view.findViewById(R.id.iv_content),view.context,Uri.fromFile(File(path)))
//        ImageUtil.showRoundImgWithStringAndRadius(view.findViewById(R.id.iv_content),view.context,Uri.fromFile(File(path)).toString(),3f)
    }

    fun setTimestamp(msg: V2TIMMessage, position: Int, mAdapter: BaseChatAdapter) {
        val timestamp = view.findViewById<TextView>(R.id.tv_time)
        timestamp?.let {
            if (position == 0) {
                timestamp.visibility = View.VISIBLE
                timestamp.text =DateTimeUtil.getTimeFormatText(Date(msg.timestamp * 1000))
            } else {
                val preMsg = mAdapter.getItem(position - 1) as V2TIMMessage
                if (msg.timestamp - preMsg.timestamp <= 5 * 60){
                    timestamp.visibility = View.GONE
                } else {
                    timestamp.text = DateTimeUtil.getTimeFormatText(Date(msg.timestamp * 1000))
                    timestamp.visibility = View.VISIBLE
                }
            }
        }
    }
}
