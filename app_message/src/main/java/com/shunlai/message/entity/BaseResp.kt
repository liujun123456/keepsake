package com.shunlai.message.entity

/**
 * @author Liu
 * @Date   2020/11/25
 * @mobile 18711832023
 */
open class BaseResp{
    var isSuccess=true
    var errorMsg=""

    fun buildError(errorMsg:String){
        isSuccess=false
        this.errorMsg=errorMsg
    }

}
