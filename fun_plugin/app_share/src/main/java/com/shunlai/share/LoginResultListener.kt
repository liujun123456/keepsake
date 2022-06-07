package com.shunlai.share

/**
 * @author Liu
 * @Date   2019-09-06
 * @mobile 18711832023
 */
interface LoginResultListener{
    fun onSuccess(code:String)

    fun onFail(reason: String)
}
