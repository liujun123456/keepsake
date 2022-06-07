package com.shunlai.im.adapter.viewHolder

import android.content.Context
import android.net.Uri
import android.text.TextUtils
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
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMValueCallback
import kotlinx.android.synthetic.main.item_video_chat_right_layout.view.*
import java.io.File

/**
 * @author Liu
 * @Date   2021/4/22
 * @mobile 18711832023
 */
class VideoViewProvider(var type:Int, var mContext: Context,var mAdapter: BaseChatAdapter){
    fun getViewHolder(): BaseViewHolder{
        return if (type== Constant.VIDEO_RECEIVE){
            val view=View.inflate(mContext, R.layout.item_video_chat_left_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            VideoLeftHolder(view)
        }else{
            val view=View.inflate(mContext, R.layout.item_video_chat_right_layout,null)
            view.layoutParams= ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            VideoRightHolder(view)
        }
    }


    inner class VideoLeftHolder(view: View): BaseChatViewHolder(view){
        override fun setData(msg: Any, position: Int) {
            try {
                var videoPath=""
                var snapShotPath=""
                msg as V2TIMMessage
                setTimestamp(msg,position,mAdapter)
                loadAvatar(msg.faceUrl,msg.sender?:"")
                msg.videoElem.getSnapshotUrl(object : V2TIMValueCallback<String>{
                    override fun onSuccess(t: String?) {
                        snapShotPath=t?:""
                        loadImg(snapShotPath)
                    }

                    override fun onError(code: Int, desc: String?) {
                        loadImg(snapShotPath)
                    }

                })
                msg.videoElem.getVideoUrl(object : V2TIMValueCallback<String>{
                    override fun onSuccess(t: String?) {
                        videoPath=t?:""
                    }

                    override fun onError(code: Int, desc: String?) {

                    }

                })

                view.findViewById<ImageView>(R.id.iv_content).setOnClickListener {
                    val params= mutableMapOf<String,Any?>()
                    params[RunIntentKey.CAMERA_IMG_PATH]=snapShotPath
                    params[RunIntentKey.CAMERA_VIDEO_PATH]=Uri.parse(videoPath)
                    RouterManager.startActivityWithParams(BundleUrl.VIDEO_VIEW_ACTIVITY,mContext as FragmentActivity,params)
                }
            }catch (e:Exception){

            }
        }
    }

    inner class VideoRightHolder(view: View): BaseChatViewHolder(view){
        override fun setData(msg: Any, position: Int) {
            try {
                var snapShotPath=""
                msg as V2TIMMessage
                setTimestamp(msg,position,mAdapter)
                loadAvatar(msg.faceUrl,msg.sender?:"")


                if (!TextUtils.isEmpty(msg.videoElem.snapshotPath)){
                    snapShotPath=msg.videoElem.snapshotPath
                    loadLocalImg(snapShotPath)
                }else{
                    msg.videoElem.getSnapshotUrl(object : V2TIMValueCallback<String>{
                        override fun onSuccess(t: String?) {
                            snapShotPath=t?:""
                            loadImg(snapShotPath)
                        }

                        override fun onError(code: Int, desc: String?) {
                            loadImg(snapShotPath)
                        }

                    })
                }

                view.findViewById<ImageView>(R.id.iv_content).setOnClickListener {
                    val params= mutableMapOf<String,Any?>()
                    params[RunIntentKey.CAMERA_IMG_PATH]=snapShotPath
                    params[RunIntentKey.CAMERA_VIDEO_PATH]=Uri.fromFile(File(msg.videoElem.videoPath))
                    RouterManager.startActivityWithParams(BundleUrl.VIDEO_VIEW_ACTIVITY,mContext as FragmentActivity,params)
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
