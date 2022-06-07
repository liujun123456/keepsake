package com.shunlai.message.main

import com.shunlai.im.entity.ConversionItem
import com.shunlai.message.entity.resp.HomeMsgResp

/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
interface MessageView {
    fun getHomeMsg(data: HomeMsgResp)

    fun addConversion(conversationList: MutableList<ConversionItem>)

    fun updateConversion(conversationList: MutableList<ConversionItem>)
}
