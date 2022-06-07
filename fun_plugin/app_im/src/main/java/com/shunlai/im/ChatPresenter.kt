package com.shunlai.im

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.toast
import com.shunlai.im.entity.ChatGoodsBean
import com.shunlai.im.inter.ImLoadMsgInterface
import com.shunlai.im.inter.ImMessageListener
import com.shunlai.im.utils.MsgUtil
import com.shunlai.net.util.GsonUtil
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMSendCallback
import java.lang.Exception
import java.util.*

/**
 * @author Liu
 * @Date   2021/4/22
 * @mobile 18711832023
 */
class ChatPresenter(var mContext: FragmentActivity, var mView: ChatView) : ImMessageListener,
    ImLoadMsgInterface, V2TIMSendCallback<V2TIMMessage> {
    val toUserId by lazy {
        mContext.intent.getStringExtra(RunIntentKey.TO_USER_ID) ?: ""
    }

    val toUserName by lazy {
        mContext.intent.getStringExtra(RunIntentKey.TO_USER_NAME) ?: ""
    }

    var chatGoods: ChatGoodsBean? = null

    var msgList = mutableListOf<V2TIMMessage>()

    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                HANDLER_MESSAGE_REFRESH_LIST -> mView.showDates()
                HANDLER_MESSAGE_SELECT_LAST -> mView.scrollToPosition(msgList.size - 1)
                HANDLER_MESSAGE_SELECT_POSITION -> {
                    mView.scrollToPosition(msg.arg1)
                }
            }
        }
    }

    companion object {
        const val HANDLER_MESSAGE_REFRESH_LIST = 0
        const val HANDLER_MESSAGE_SELECT_LAST = 1
        const val HANDLER_MESSAGE_SELECT_POSITION = 2
        const val TIME_DELAY_REFRESH_SELECT_LAST = 100L
    }

    init {
        ImManager.addMsgListener(this)
        try {
            chatGoods = GsonUtil.fromJson(
                mContext.intent.getStringExtra(RunIntentKey.GOODS),
                ChatGoodsBean::class.java
            )
        } catch (e: Exception) {
        }
    }

    fun loadMsg() {
        ImManager.loadC2CMsg(toUserId, null, this)
    }

    fun loadHistory() {
        ImManager.loadC2CMsg(toUserId, if (msgList.size > 0) msgList[0] else null, this)
    }

    fun sendTextMsg(value: String) {
        if (TextUtils.isEmpty(value)) {
            return
        }
        if (TextUtils.isEmpty(V2TIMManager.getInstance().loginUser)) {
            toast("用户已下线")
            return
        }
        val currentMsg = MsgUtil.sendTextMsg(value, toUserId, this)
        if (currentMsg != null) {
            msgList.add(currentMsg)
            refreshData()
            scrollBottom()
        }
    }

    fun sendImgMsg(uri: Uri?) {
        if (TextUtils.isEmpty(V2TIMManager.getInstance().loginUser)) {
            toast("用户已下线")
            return
        }
        if (uri != null) {
            val msg = MsgUtil.sendImgMsgWithUri(uri, toUserId, this)
            if (msg != null) {
                msgList.add(msg)
                refreshData()
                scrollBottom()
            }
        }
    }

    fun sendVideoMsgWithUri(uri: Uri) {
        if (TextUtils.isEmpty(V2TIMManager.getInstance().loginUser)) {
            toast("用户已下线")
            return
        }
        val msg = MsgUtil.sendVideoMsgWithUri(uri, toUserId, this)
        if (msg != null) {
            msgList.add(msg)
            refreshData()
            scrollBottom()
        }
    }

    fun sendVideoMsgWithPath(videoPath: String?, duration: Long, imgPath: String?) {
        if (TextUtils.isEmpty(V2TIMManager.getInstance().loginUser)) {
            toast("用户已下线")
            return
        }
        val msg = MsgUtil.sendVideoMsgWithPath(videoPath, duration, imgPath, toUserId, this)
        if (msg != null) {
            msgList.add(msg)
            refreshData()
            scrollBottom()
        }
    }

    fun sendGoods() {
        if (TextUtils.isEmpty(V2TIMManager.getInstance().loginUser)) {
            toast("用户已下线")
            return
        }
        chatGoods?.let {
            val msg = MsgUtil.sendCustomGoods(it, toUserId, "goods", this)
            if (msg != null) {
                msgList.add(msg)
                refreshData()
                scrollBottom()
            }
        }
    }


    override fun onRecvNewMessage(msg: V2TIMMessage?) {
        super.onRecvNewMessage(msg)
        if (msg?.sender != toUserId) {
            return
        }
        msg.let {
            msgList.add(it)
            refreshData()
            scrollBottom()
        }
    }

    fun scrollBottom() {
        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST)
        handler.sendEmptyMessageDelayed(
            HANDLER_MESSAGE_SELECT_LAST,
            TIME_DELAY_REFRESH_SELECT_LAST
        )
    }

    private fun refreshData() {
        handler.removeMessages(HANDLER_MESSAGE_REFRESH_LIST)
        handler.sendEmptyMessageDelayed(
            HANDLER_MESSAGE_REFRESH_LIST,
            TIME_DELAY_REFRESH_SELECT_LAST
        )

    }

    override fun onSuccess(t: List<V2TIMMessage>) {
        Collections.reverse(t)
        msgList.addAll(0, t)
        if (msgList.size == t.size) {
            refreshData()
            scrollBottom()
        } else {
            mView.notifyRange(0, t.size)
        }
    }

    override fun onFail(code: Int, desc: String?) {

    }

    fun onDestroy() {
        ImManager.removeMsgListener(this)
    }

    override fun onSuccess(t: V2TIMMessage?) {
        t?.let {
            try {
                if (t.elemType == V2TIMMessage.V2TIM_ELEM_TYPE_TEXT) {
                    mView.pushMessage(1, t.textElem.text ?: "")
                } else if (it.elemType == V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE) {
                    mView.pushMessage(2, "图片")
                } else if (it.elemType == V2TIMMessage.V2TIM_ELEM_TYPE_VIDEO) {
                    mView.pushMessage(3, "视频")
                } else if (it.elemType == V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM) {
                    if (it.customElem.description == "goods") {
                        mView.pushMessage(4, String(it.customElem.data))
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun onProgress(progress: Int) {

    }

    override fun onError(code: Int, desc: String?) {
        if (code == 20007) {
            handler.postDelayed( {
                    val msg = MsgUtil.insertCustomBlack(toUserId)
                    msg?.let {
                        msgList.add(it)
                        refreshData()
                        scrollBottom()
                    }
                }, 500
            )

        } else {
            refreshData()
        }
    }
}
