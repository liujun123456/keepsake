package com.shunlai.im.inter

import com.tencent.imsdk.v2.V2TIMMessage

/**
 * @author Liu
 * @Date   2021/4/22
 * @mobile 18711832023
 */
interface ImLoadMsgInterface {
    fun onSuccess(t: List<V2TIMMessage>)

    fun onFail(code: Int, desc: String?){

    }
}
