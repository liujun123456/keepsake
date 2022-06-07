package com.shunlai.share

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.shunlai.common.BaseApplication
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.FileUtil
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory


/**
 * @author Liu
 * @Date   2021/4/26
 * @mobile 18711832023
 */
class WeChatUtil {
    private var mWxApi: IWXAPI?=null
    private var mLoginListener: LoginResultListener?=null

    init {
        mWxApi = WXAPIFactory.createWXAPI(BaseApplication.mInstance,
            Constant.WEIXIN_APPID, true)
        mWxApi?.registerApp(Constant.WEIXIN_APPID)
    }
    companion object{
        const val THUMB_SIZE = 150
        private var mInstance: WeChatUtil?=null
        @JvmStatic
        fun getInstance(): WeChatUtil {
            if (mInstance ==null){
                synchronized(WeChatUtil::class){
                    if (mInstance ==null){
                        mInstance =
                            WeChatUtil()
                    }
                }
            }
            return mInstance!!
        }
    }

    fun doLogin(loginListener: LoginResultListener){
        mLoginListener=loginListener
        val req= SendAuth.Req()
        req.scope="snsapi_userinfo"
        req.state="wechat_sdk_demo_test"
        mWxApi?.sendReq(req)
    }

    /**
     * 分享网址到微信好友
     */
    fun shareWeChatWithWeb(url:String,title:String,desc:String,imgBitmap:Bitmap,isVideo:Boolean=false){
        realWebShare(url,title,desc,imgBitmap,SendMessageToWX.Req.WXSceneSession,isVideo)
    }

    /**
     * 分享网址到朋友圈
     */
    fun shareCircleWithWeb(url:String,title:String,desc:String,imgBitmap:Bitmap,isVideo:Boolean=false){
        realWebShare(url,title,desc,imgBitmap,SendMessageToWX.Req.WXSceneTimeline,isVideo)
    }


    /**
     * 分享图片到微信好友
     */
    fun shareWeChatWithImg(bmp:Bitmap){

        realImgShare(bmp,SendMessageToWX.Req.WXSceneSession)
    }

    /**
     * 分享图片到朋友圈
     */
    fun shareCircleWithImg(bmp:Bitmap){

        realImgShare(bmp,SendMessageToWX.Req.WXSceneTimeline)
    }


    /**
     * 微信登陆回调
     */
    fun notifyLoginResult(isSuccess: Boolean,code:String?,errorMsg:String?){
        if (isSuccess){
            mLoginListener?.onSuccess(code?:"")
        }else{
            mLoginListener?.onFail(errorMsg?:"")
        }
        mLoginListener=null
    }

    private fun realImgShare(bmp:Bitmap,scene:Int){
        val imgObj = WXImageObject(bmp)
        val msg = WXMediaMessage()
        msg.mediaObject = imgObj

        val thumbBmp = Bitmap.createScaledBitmap(bmp,
            THUMB_SIZE,
            THUMB_SIZE,true)
        msg.thumbData =
            FileUtil.bmpToByteArray(thumbBmp, true)

        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("img")
        req.message = msg
        req.scene=scene
        mWxApi?.sendReq(req)
    }

    private fun realWebShare(url:String,title:String,desc:String,imgBitmap:Bitmap,scene:Int,isVideo:Boolean=false){
        val webPage = WXWebpageObject()
        webPage.webpageUrl=url

        val msg = WXMediaMessage(webPage)
        msg.title=title
        msg.description=desc

        val thumbBmp = Bitmap.createScaledBitmap(imgBitmap,
            THUMB_SIZE,
            THUMB_SIZE,true)

        val newBmp = Bitmap.createBitmap(
            THUMB_SIZE,
            THUMB_SIZE, Bitmap.Config.ARGB_8888)
        val cv = Canvas(newBmp)

        cv.drawBitmap(thumbBmp,0f,0f,null)
        thumbBmp.recycle()

        if (isVideo){
            val videoBitmap=BitmapFactory.decodeResource(BaseApplication.mInstance.resources, R.mipmap.ui_share_video_icon)
            cv.drawBitmap(videoBitmap,45f,45f,null)
            videoBitmap.recycle()
        }
        msg.thumbData=  FileUtil.bmpToByteArray(newBmp, true)

        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("webpage")
        req.message =msg
        req.scene =scene

        mWxApi?.sendReq(req)

    }

    private fun buildTransaction(type: String?): String? {
        return if (type == null) System.currentTimeMillis()
            .toString() else type + System.currentTimeMillis()
    }

}
