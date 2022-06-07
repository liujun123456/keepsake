package com.shunlai.mine.entity.resp

import com.shunlai.mine.entity.BaseResp
import com.shunlai.mine.entity.bean.LoginMember


/**
 * @author Liu
 * @Date   2021/4/26
 * @mobile 18711832023
 */
class LoginResp: BaseResp() {
    var accessToken:String?=null
    var hasMobile:Int?=null
    var userSig:String?=null
    var member: LoginMember?=null
}
