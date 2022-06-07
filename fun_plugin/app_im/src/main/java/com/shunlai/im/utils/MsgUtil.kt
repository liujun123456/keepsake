package com.shunlai.im.utils

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.shunlai.common.BaseApplication
import com.shunlai.common.utils.FileUtil
import com.shunlai.im.entity.ChatGoodsBean
import com.shunlai.net.util.GsonUtil
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMSendCallback

/**
 * @author Liu
 * @Date   2021/4/22
 * @mobile 18711832023
 */
object MsgUtil {

    fun sendTextMsg(value:String,toUserId:String,callBack: V2TIMSendCallback<V2TIMMessage>?=null):V2TIMMessage?{
        val msg=V2TIMManager.getMessageManager().createTextMessage(value)
        V2TIMManager.getMessageManager().sendMessage(msg,toUserId,null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT, false, null,callBack)
        return msg
    }

    fun sendImgMsgWithUri(uri: Uri,toUserId:String,callBack: V2TIMSendCallback<V2TIMMessage>?=null):V2TIMMessage?{
        val path= ImFileUtil.getPathFromUri(uri)
        val msg=V2TIMManager.getMessageManager().createImageMessage(path)
        V2TIMManager.getMessageManager().sendMessage(msg,toUserId,null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT, false, null,callBack)
        return msg
    }

    fun sendImgMsgWithPath(path: String,toUserId:String,callBack: V2TIMSendCallback<V2TIMMessage>?=null):V2TIMMessage?{
        val msg=V2TIMManager.getMessageManager().createImageMessage(path)
        V2TIMManager.getMessageManager().sendMessage(msg,toUserId,null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT, false, null,callBack)
        return msg
    }

    fun sendVideoMsgWithUri(uri: Uri,toUserId:String,callBack: V2TIMSendCallback<V2TIMMessage>?=null):V2TIMMessage?{
        val msg= buildVideoMessage(uri) ?: return null
        V2TIMManager.getMessageManager().sendMessage(msg,toUserId,null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT, false, null,callBack)
        return msg
    }

    fun sendVideoMsgWithPath(videoPath:String?,duration:Long,imgPath:String?,
                             toUserId:String,callBack: V2TIMSendCallback<V2TIMMessage>?=null):V2TIMMessage?{
        val msg= V2TIMManager.getMessageManager().createVideoMessage(videoPath,"mp4", duration.toInt() / 1000,imgPath)
            ?: return null
        V2TIMManager.getMessageManager().sendMessage(msg,toUserId,null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT, false, null,callBack)
        return msg

    }

    fun sendCustomGoods(chatGoods: ChatGoodsBean,toUserId:String,desc:String,callBack: V2TIMSendCallback<V2TIMMessage>?=null):V2TIMMessage?{
        val data=GsonUtil.toJson(chatGoods)
        val msg= V2TIMManager.getMessageManager().createCustomMessage(data.toByteArray(),desc,null)
            ?: return null
        V2TIMManager.getMessageManager().sendMessage(msg,toUserId,null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT, false, null,callBack)
        return msg
    }

    fun insertCustomBlack(toUserId:String):V2TIMMessage?{
        val msg= V2TIMManager.getMessageManager().createCustomMessage(byteArrayOf(),"black",null)
            ?: return null
        V2TIMManager.getMessageManager().insertC2CMessageToLocalStorage(msg,toUserId,toUserId,null)
        return msg
    }


    private fun buildVideoMessage(uri: Uri):V2TIMMessage?{
        try {
            val videoPath= ImFileUtil.getPathFromUri(uri)
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(videoPath)
            val mimeType =MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
            if (mimeType!=null&&mimeType.contains("video")){
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(videoPath)
                val sDuration =mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) //时长(毫秒)
                val bitmap =
                    mmr.getFrameAtTime(0, MediaMetadataRetriever.OPTION_NEXT_SYNC) ?: return null //缩略图
                val imgPath: String = FileUtil.saveBitmap(BaseApplication.mInstance, bitmap)
                return V2TIMManager.getMessageManager().createVideoMessage(videoPath,"mp4", sDuration.toInt() / 1000,imgPath)
            }
        }catch (e:Exception){

        }
        return null
    }

}
