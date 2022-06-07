package com.shunlai.mine.entity.req

/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
class UnBindWeChatReq {
    var member:Data?=null
    var memberId:String?=""

    class Data(var appOpenId:String?="")

    fun buildData(memberId:String,appOpenId:String):UnBindWeChatReq{
        this.memberId=memberId
        this.member=Data(appOpenId)
        return this
    }
}
