package com.shunlai.ugc.entity

/**
 * @author Liu
 * @Date   2020/11/25
 * @mobile 18711832023
 */
open class BaseResp{
    var isSuccess=true
    var errorMsg=""
    var errorCode=200

    fun buildError(errorMsg:String){
        isSuccess=false
        this.errorMsg=errorMsg
    }

}
