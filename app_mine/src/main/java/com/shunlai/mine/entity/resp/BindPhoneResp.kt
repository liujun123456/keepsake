package com.shunlai.mine.entity.resp

import com.shunlai.mine.entity.BaseResp

/**
 * @author Liu
 * @Date   2021/4/28
 * @mobile 18711832023
 */
class BindPhoneResp: BaseResp() {
    var accessToken:String?=null
    var hasMobile:Int?=null
    var userSig:String?=null
    var member: BindPhoneBean?=null

    class BindPhoneBean{
        var id:String?=""
        var mobile:String?=""
    }
}
