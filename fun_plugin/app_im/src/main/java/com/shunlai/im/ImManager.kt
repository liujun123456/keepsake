package com.shunlai.im

import android.app.Application
import android.text.TextUtils
import android.util.Log
import com.shunlai.im.entity.ConversionItem
import com.shunlai.im.face.FaceManager
import com.shunlai.im.inter.*
import com.tencent.imsdk.v2.*
import com.tencent.imsdk.v2.V2TIMManager.V2TIM_STATUS_LOGINED
import com.tencent.imsdk.v2.V2TIMManager.V2TIM_STATUS_LOGINING


/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
object ImManager{

    private val msgListenerList:MutableList<ImMessageListener> = mutableListOf()
    private val conversationListenerList:MutableList<ImConversationInterface> = mutableListOf()

    /**
     * 初始化
     */
    fun init(context:Application){
        FaceManager.loadFaceFiles()
        val config = V2TIMSDKConfig()
        config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_INFO)
        V2TIMManager.getInstance().initSDK(context,1400478653,config,object : V2TIMSDKListener(){
            override fun onConnecting() {
                super.onConnecting()
                Log.e("----->","im服务器连接中")
            }

            override fun onConnectSuccess() {
                super.onConnectSuccess()
                initMessageListener()
                initConversationListener()
                Log.e("----->","im服务器连接成功")
            }

            override fun onConnectFailed(code: Int, error: String?) {
                super.onConnectFailed(code, error)
                Log.e("----->","im服务器连接失败$error----$code")
            }

            override fun onKickedOffline() {
                super.onKickedOffline()
            }

            override fun onSelfInfoUpdated(info: V2TIMUserFullInfo?) {
                super.onSelfInfoUpdated(info)
            }
        })
    }

    private fun initMessageListener(){
        V2TIMManager.getMessageManager().addAdvancedMsgListener(object :V2TIMAdvancedMsgListener(){

            override fun onRecvMessageRevoked(msgID: String?) {
                super.onRecvMessageRevoked(msgID)
                msgListenerList.forEach {
                    it.onRecvMessageRevoked(msgID)
                }
            }

            override fun onRecvNewMessage(msg: V2TIMMessage?) {
                super.onRecvNewMessage(msg)
                msgListenerList.forEach {
                    it.onRecvNewMessage(msg)
                }
            }

            override fun onRecvC2CReadReceipt(receiptList: MutableList<V2TIMMessageReceipt>?) {
                super.onRecvC2CReadReceipt(receiptList)
                msgListenerList.forEach {
                    it.onRecvC2CReadReceipt(receiptList)
                }
            }
        })
    }

    private fun initConversationListener(){
        V2TIMManager.getConversationManager().setConversationListener(object :V2TIMConversationListener(){
            override fun onNewConversation(conversationList: MutableList<V2TIMConversation>?) {
                super.onNewConversation(conversationList)
                val list= mutableListOf<ConversionItem>()
                conversationList?.forEach {
                    list.add(buildConversionItem(it))

                }
                conversationListenerList.forEach {
                    it.addConversation(list)
                }
            }

            override fun onConversationChanged(conversationList: MutableList<V2TIMConversation>?) {
                super.onConversationChanged(conversationList)
                val list= mutableListOf<ConversionItem>()
                conversationList?.forEach {
                    list.add(buildConversionItem(it))

                }
                conversationListenerList.forEach {
                    it.addConversation(list)
                }
            }
        })
    }


    /**
     * 登陆
     */
    fun login(userId:String,userSig:String,inter:ImLoginInterface){
        if (V2TIMManager.getInstance().loginStatus==V2TIM_STATUS_LOGINED&&V2TIMManager.getInstance().loginUser==userId){
            inter.imLoginSuccess()
            return
        }
        V2TIMManager.getInstance().login(userId,userSig,object : V2TIMCallback{
            override fun onSuccess() {
                inter.imLoginSuccess()
            }

            override fun onError(code: Int, desc: String?) {
                inter.imLoginFailed(desc?:"未知异常")
            }
        })
    }

    /**
     * 登出
     */
    fun loginOut(inter:ImLoginOutInterface){
        if (V2TIMManager.getInstance().loginStatus==V2TIM_STATUS_LOGINED||
            V2TIMManager.getInstance().loginStatus==V2TIM_STATUS_LOGINING)
        V2TIMManager.getInstance().logout(object : V2TIMCallback{
            override fun onSuccess() {
                inter.imLoginOutSuccess()
            }

            override fun onError(code: Int, desc: String?) {
                inter.imLoginOutFailed(desc?:"未知异常")
            }
        })
    }

    /**
     * 加载会话列表
     */
    fun loadConversation(inter:ImConversationInterface){
        if (TextUtils.isEmpty(V2TIMManager.getInstance().loginUser)){
            return
        }
        if (!conversationListenerList.contains(inter)){
            conversationListenerList.add(inter)
        }
        V2TIMManager.getConversationManager().getConversationList(0,50,object :V2TIMValueCallback<V2TIMConversationResult>{
            override fun onSuccess(t: V2TIMConversationResult?) {
                t?.let {
                    val list= mutableListOf<ConversionItem>()
                    it.conversationList.forEach {bean->
                        list.add(buildConversionItem(bean))
                    }
                    conversationListenerList.forEach {
                        it.addConversation(list)
                    }
                    if (!it.isFinished){
                        V2TIMManager.getConversationManager().getConversationList(t.nextSeq,50,object :V2TIMValueCallback<V2TIMConversationResult>{
                            override fun onSuccess(t: V2TIMConversationResult?) {
                                t?.let {data->
                                    val list= mutableListOf<ConversionItem>()
                                    data.conversationList.forEach {bean->
                                        list.add(buildConversionItem(bean))
                                    }
                                    conversationListenerList.forEach {
                                        it.addConversation(list)
                                    }
                                }
                            }

                            override fun onError(code: Int, desc: String?) {

                            }

                        })
                    }
                }
            }

            override fun onError(code: Int, desc: String?) {

            }
        })
    }

    private fun buildConversionItem(bean:V2TIMConversation):ConversionItem{
        val item=ConversionItem()
        item.conversationID=bean.conversationID
        item.faceUrl=bean.faceUrl
        item.groupId=bean.groupID
        item.conversionType=bean.type
        item.showName=bean.showName
        item.time=bean.lastMessage?.timestamp
        item.userId=bean.userID
        item.unReadCount=bean.unreadCount
        if (bean.lastMessage.elemType== V2TIMMessage.V2TIM_ELEM_TYPE_VIDEO){
            item.lastMessageContent="视频消息"
        }else if (bean.lastMessage.elemType== V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE){
            item.lastMessageContent="图片消息"
        }else if (bean.lastMessage.elemType== V2TIMMessage.V2TIM_ELEM_TYPE_TEXT){
            item.lastMessageContent=bean.lastMessage.textElem.text
        }else{
            item.lastMessageContent="自定义消息"
        }
        return item
    }

    /**
     * 添加消息监听
     */
    fun addMsgListener(listener:ImMessageListener){
        if (!msgListenerList.contains(listener)){
            msgListenerList.add(listener)
        }
    }

    /**
     * 标记消息为已读
     */
    fun markerMessageAsRead(userId:String){
        V2TIMManager.getMessageManager().markC2CMessageAsRead(userId,object :V2TIMCallback{
            override fun onSuccess() {

            }

            override fun onError(code: Int, desc: String?) {

            }

        })
    }

    /**
     * 删除消息监听
     */
    fun removeMsgListener(listener:ImMessageListener){
        if (msgListenerList.contains(listener)){
            msgListenerList.remove(listener)
        }
    }

    /**
     * 加载一对一聊天历史记录
     */
    fun loadC2CMsg(toUserId:String, lastMsg:V2TIMMessage?=null, mInterface: ImLoadMsgInterface){
            V2TIMManager.getMessageManager().getC2CHistoryMessageList(toUserId,20,lastMsg,object :V2TIMValueCallback<List<V2TIMMessage>>{
                override fun onSuccess(t: List<V2TIMMessage>?) {
                    mInterface.onSuccess(t?: mutableListOf())
                }

                override fun onError(code: Int, desc: String?) {
                    mInterface.onFail(code,desc)
                }
            })
    }
}
