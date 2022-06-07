package com.shunlai.im.inter

import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMMessageReceipt

/**
 * @author Liu
 * @Date   2021/4/22
 * @mobile 18711832023
 */
interface ImMessageListener {
    fun onRecvMessageRevoked(msgID: String?){

    }

    fun onRecvNewMessage(msg: V2TIMMessage?){

    }

    fun onRecvC2CReadReceipt(receiptList: MutableList<V2TIMMessageReceipt>?){

    }
}
