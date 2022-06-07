package com.shunlai.message.main

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.bean.MessageNotifyEvent
import com.shunlai.common.utils.TimeUtil
import com.shunlai.im.ImManager
import com.shunlai.im.entity.ConversionItem
import com.shunlai.im.inter.ImConversationInterface
import com.shunlai.message.MessageViewModel
import com.shunlai.message.entity.HomeMsgBean
import com.shunlai.message.entity.event.NotifyMsgEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
class MessagePresenter(var mContext:Context,var mView:MessageView):ImConversationInterface{
    private val mViewModel by lazy {
        ViewModelProvider(mContext as FragmentActivity).get(MessageViewModel::class.java)
    }

    init {
        initViewModel()
        ImManager.loadConversation(this)
        EventBus.getDefault().register(this)
    }

    private fun initViewModel(){
        mViewModel.homeMsgResp.observe(mContext as FragmentActivity, Observer {
            if (it.isSuccess){
                mView.getHomeMsg(it)
            }
            val value=(it?.commentNum?:0)+(it?.pushNum?:0)+(it?.likesNum?:0)+(it?.systemNum?:0)+(it?.followNum?:0)
            if (value>0){
                EventBus.getDefault().post(MessageNotifyEvent(true))
            }else{
                EventBus.getDefault().post(MessageNotifyEvent(false))
            }
        })
    }

    fun queryTotalMsgNum(){
        mViewModel.queryHomeMsg()
    }

    fun updateCollectRead(){
        mViewModel.updateCollect()
    }

    fun updateAttentionRead(){
        mViewModel.updateAttention()
    }

    fun updateCommentRead(){
        mViewModel.updateComment()
    }

    fun reloadConversation(){
        ImManager.loadConversation(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateSysMsgRead(event: NotifyMsgEvent){
        if (event.type==1){
            mViewModel.updateSysMsg()
        }else{
            mViewModel.updatePushMsg()
        }

    }

    fun onDestroy(){
        EventBus.getDefault().unregister(this)
    }

    fun buildHomeMsgBean(item:ConversionItem):HomeMsgBean{
        val homeMsgBean=HomeMsgBean()
        homeMsgBean.title=item.showName
        homeMsgBean.content=item.lastMessageContent
        homeMsgBean.img=item.faceUrl
        homeMsgBean.count=item.unReadCount
        homeMsgBean.type=2
        homeMsgBean.time=TimeUtil.getTime(item.time?.times(1000)?:System.currentTimeMillis())
        homeMsgBean.conversionType=item.conversionType
        homeMsgBean.conversationID=item.conversationID
        homeMsgBean.userId=item.userId
        homeMsgBean.groupId=item.groupId
        return homeMsgBean
    }

    override fun addConversation(conversationList: MutableList<ConversionItem>) {
        conversationList.forEach {
            if (it.unReadCount?:0>0){
                EventBus.getDefault().post(MessageNotifyEvent(true))
                return@forEach
            }
        }
        mView.addConversion(conversationList)
    }

    override fun updateConversation(conversationList: MutableList<ConversionItem>) {
        conversationList.forEach {
            if (it.unReadCount?:0>0){
                EventBus.getDefault().post(MessageNotifyEvent(true))
                return@forEach
            }
        }
        mView.updateConversion(conversationList)
    }

}
