package com.shunlai.im.adapter.viewHolder

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.im.R
import com.shunlai.im.adapter.BaseChatAdapter
import com.shunlai.im.utils.Constant
import com.shunlai.router.RouterManager
import com.tencent.imsdk.v2.V2TIMImageElem
import com.tencent.imsdk.v2.V2TIMMessage
import kotlinx.android.synthetic.main.item_chat_image_right_layout.view.*

/**
 * @author Liu
 * @Date   2021/4/22
 * @mobile 18711832023
 */
class ImageViewProvider(var type:Int, var mContext: Context,var mAdapter: BaseChatAdapter){
    fun getViewHolder(): BaseViewHolder{
        return if (type==Constant.IMAGE_RECEIVE){
            val view=View.inflate(mContext, R.layout.item_chat_image_left_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            ImageLeftHolder(view)
        }else{
            val view=View.inflate(mContext, R.layout.item_chat_image_right_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            ImageTxtRightHolder(view)
        }
    }


    inner class ImageLeftHolder(view: View): BaseChatViewHolder(view){
        override fun setData(msg: Any, position: Int) {
            try {
                msg as V2TIMMessage
                setTimestamp(msg,position,mAdapter)
                loadAvatar(msg.faceUrl,msg.sender?:"")
                var imgPath:String?=""
                val imgList=msg.imageElem.imageList
                imgList.forEach {
                    if (it.type== V2TIMImageElem.V2TIM_IMAGE_TYPE_THUMB){
                        loadImg(it.url)
                    }else if (it.type==V2TIMImageElem.V2TIM_IMAGE_TYPE_LARGE){
                        imgPath=it.url
                    }
                }
                view.findViewById<ImageView>(R.id.iv_content).setOnClickListener {
                    val params= mutableMapOf<String,Any?>()
                    params[RunIntentKey.IMAGE_URL]=imgPath
                    RouterManager.startActivityWithParams(BundleUrl.PHOTO_SIMPLE_PREVIEW_PATH_ACTIVITY,mContext as FragmentActivity,params)
                }
            }catch (e:Exception){

            }
        }
    }

    inner class ImageTxtRightHolder(view: View): BaseChatViewHolder(view){
        override fun setData(msg: Any, position: Int) {
            try {
                msg as V2TIMMessage
                setTimestamp(msg,position,mAdapter)
                loadAvatar(msg.faceUrl,msg.sender?:"")
                var imgPath:String?=""
                if (!msg.imageElem.imageList.isNullOrEmpty()){
                    msg.imageElem.imageList.forEach {
                        if (it.type== V2TIMImageElem.V2TIM_IMAGE_TYPE_THUMB){
                            loadImg(it.url)
                        }else if (it.type==V2TIMImageElem.V2TIM_IMAGE_TYPE_LARGE){
                            imgPath=it.url
                        }
                    }
                }else{
                    loadLocalImg(msg.imageElem.path)
                    imgPath=msg.imageElem.path
                }

                view.findViewById<ImageView>(R.id.iv_content).setOnClickListener {
                    val params= mutableMapOf<String,Any?>()
                    params[RunIntentKey.IMAGE_URL]=imgPath
                    RouterManager.startActivityWithParams(BundleUrl.PHOTO_SIMPLE_PREVIEW_PATH_ACTIVITY,mContext as FragmentActivity,params)
                }

                if (msg.status== V2TIMMessage.V2TIM_MSG_STATUS_SEND_FAIL){
                    view.iv_send_fail.visibility=View.VISIBLE
                }else{
                    view.iv_send_fail.visibility=View.INVISIBLE
                }
            }catch (e:Exception){

            }
        }
    }
}
