package com.shunlai.mine.entity.req

/**
 * @author Liu
 * @Date   2021/4/26
 * @mobile 18711832023
 */
class LoginReq {
    var channel:String="App-Android"
    var code:String?=""
    var phone:String?=""

    fun buildParams(phoneNum:String,verifyCode:String){
        phone=phoneNum
        code=verifyCode
    }
}
