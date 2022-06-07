package com.shunlai.main.entities.resp

/**
 * @author Liu
 * @Date   2021/6/22
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
