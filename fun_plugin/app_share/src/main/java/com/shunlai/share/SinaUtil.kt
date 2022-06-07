package com.shunlai.share

import android.content.Context
import com.sina.weibo.sdk.api.WebpageObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.openapi.IWBAPI
import com.sina.weibo.sdk.openapi.WBAPIFactory


/**
 * @author Liu
 * @Date   2021/9/17
 * @mobile 18711832023
 */
class SinaUtil(mContext:Context) {
    private var mApi: IWBAPI?=null

    init {
        val authInfo = AuthInfo(mContext, "1194080161", "REDIRECT_URL", "SCOPE")
        mApi=WBAPIFactory.createWBAPI(mContext)
        mApi?.registerApp(mContext,authInfo)
    }


    fun doShare(content:String,url:String){
        val message = WeiboMultiMessage()
        val webObject = WebpageObject()
        webObject.actionUrl=url
        webObject.description=content
        message.mediaObject=webObject
        mApi?.shareMessage(message,true)
    }

}
