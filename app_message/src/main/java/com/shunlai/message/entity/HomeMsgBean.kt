package com.shunlai.message.entity

/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
class HomeMsgBean {
    var title:String?=""
    var content:String?=""
    var img:String?=""
    var count:Int?=null
    var time:String?=null
    var type:Int=0  //0系统消息 1推送消息 2IM消息

    var conversationID:String?=null
    var conversionType:Int?=null
    var userId:String?=null
    var groupId:String?=null
}
