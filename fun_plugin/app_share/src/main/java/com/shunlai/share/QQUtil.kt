package com.shunlai.share

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.shunlai.common.BaseApplication
import com.shunlai.common.utils.Constant
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_DEFAULT
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError


/**
 * @author Liu
 * @Date   2021/9/14
 * @mobile 18711832023
 */
class QQUtil{

    private var mTencent:Tencent?=null

    init {
        mTencent=Tencent.createInstance(Constant.QQ_APPID,BaseApplication.mInstance,"${BaseApplication.mInstance.packageName}.fileProvider")
    }

    companion object{
        private var mInstance:QQUtil?=null

        @JvmStatic
        fun getInstance():QQUtil{
            if (mInstance ==null){
                synchronized(QQUtil::class){
                    if (mInstance ==null){
                        mInstance =QQUtil()
                    }
                }
            }
            return mInstance!!
        }
    }

    /**
     * 分享网址到QQ
     */
    fun shareQQWithWeb(activity: FragmentActivity,url:String, title:String, desc:String,imgUrl:String){
        realWebShare(activity,url,title,desc, imgUrl)
    }

    /**
     * 分享网址到QQ空间
     */
    fun shareQQZONEWithWeb(activity: FragmentActivity,url:String, title:String, desc:String,imgUrl:String){
        realWebShare(activity,url,title,desc, imgUrl,1)
    }


    /**
     * 分享图片到QQ
     */
    fun shareQQWithImg(activity: FragmentActivity,localUrl:String){

        realImgShare(activity, localUrl)
    }

    /**
     * 分享图片到QQ空间
     */
    fun shareQQZONEWithImg(activity: FragmentActivity,localUrl:String){

        realImgShare(activity, localUrl,1)
    }

    private fun realImgShare(activity: FragmentActivity,localUrl:String,type:Int=0){
        val params = Bundle()
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, localUrl)
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "透壳")
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN)
        if (type==0){
            mTencent?.shareToQQ(activity, params, BaseUIListener())
        }else{
            mTencent?.shareToQzone(activity, params, BaseUIListener())
        }

    }

    private fun realWebShare(activity: FragmentActivity,url:String,title:String,desc:String,imageUrl:String,type:Int=0){
        val params = Bundle()
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, SHARE_TO_QQ_TYPE_DEFAULT)
        params.putString(QQShare.SHARE_TO_QQ_TITLE,title)
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, desc)
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url)
        params.putString( QQShare.SHARE_TO_QQ_IMAGE_URL,imageUrl)
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "透壳")
        if (type==0){
            mTencent?.shareToQQ(activity, params, BaseUIListener())
        }else{
            mTencent?.shareToQzone(activity, params, BaseUIListener())
        }
    }

    class BaseUIListener:IUiListener{
        override fun onComplete(p0: Any?) {

        }

        override fun onCancel() {

        }

        override fun onWarning(p0: Int) {

        }

        override fun onError(p0: UiError?) {

        }
    }

}
