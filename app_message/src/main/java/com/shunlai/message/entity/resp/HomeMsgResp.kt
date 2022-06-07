package com.shunlai.message.entity.resp

import com.shunlai.message.entity.BaseResp

/**
 * @author Liu
 * @Date   2021/4/21
 * @mobile 18711832023
 */
class HomeMsgResp:BaseResp() {
    var followNum:Int?=null
    var systemNum:Int?=null
    var likesNum:Int?=null
    var pushNum:Int?=null
    var commentNum:Int?=null

    var sysMessage:SystemMsgResp?=null
    var pushMessage:PushMsgResp?=null
}
