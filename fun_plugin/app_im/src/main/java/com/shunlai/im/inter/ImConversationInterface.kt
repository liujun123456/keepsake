package com.shunlai.im.inter

import com.shunlai.im.entity.ConversionItem

/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
interface ImConversationInterface {
    fun addConversation(conversationList:MutableList<ConversionItem>)
    fun updateConversation(conversationList:MutableList<ConversionItem>)
}
