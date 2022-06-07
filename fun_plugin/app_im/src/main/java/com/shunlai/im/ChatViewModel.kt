package com.shunlai.im

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.shunlai.net.CoreHttpSubscriber
import com.shunlai.net.CoreHttpThrowable

/**
 * @author Liu
 * @Date   2021/7/24
 * @mobile 18711832023
 */
class ChatViewModel: ViewModel()   {
    private val lifecycleOwner: LifecycleOwner?=null

    fun blockUser(userId:String){
        val params= mutableMapOf<String,Any>()
        params["beBlackMember"]=userId
        ChatHttpManager.postByParams(lifecycleOwner,ChatApiConfig.BLOCK_USER,params).subscribe(object :
            CoreHttpSubscriber<String> {
            override fun onFailed(throwable: CoreHttpThrowable) {

            }

            override fun onSuccess(t: String?) {

            }

        })
    }

    /**
     * 推送聊天消息
     */
    fun pushMessage(memberId:String,type:Int,message:String){
        val params= mutableMapOf<String,Any>()
        params["memberId"]=memberId
        params["type"]=type
        params["message"]=message
        ChatHttpManager.getByParams(lifecycleOwner,ChatApiConfig.PUSH_MESSAGE,params).subscribe(object :
            CoreHttpSubscriber<String> {
            override fun onFailed(throwable: CoreHttpThrowable) {

            }

            override fun onSuccess(t: String?) {

            }

        })

    }
}
